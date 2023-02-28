package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.user.AccountIdAlreadyExistsException;
import com.developlife.reviewtwits.exception.user.AccountIdNotFoundException;
import com.developlife.reviewtwits.exception.user.PasswordVerifyException;
import com.developlife.reviewtwits.exception.user.AccountPasswordWrongException;
import com.developlife.reviewtwits.mapper.UserMapper;
import com.developlife.reviewtwits.message.request.user.LoginUserRequest;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import com.developlife.reviewtwits.repository.UserRepository;
import com.developlife.reviewtwits.type.UserRole;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

/**
 * @author ghdic
 * @since 2023/02/19
 */
@Service
@Log4j2
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public User login(LoginUserRequest loginUserRequest) {
        User user = getUser(loginUserRequest.accountId());
        if (!passwordEncoder.matches(loginUserRequest.accountPw(), user.getAccountPw())) {
            throw new AccountPasswordWrongException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    public User register(RegisterUserRequest registerUserRequest, Set<UserRole> roles) {
        Optional<User> user = userRepository.findByAccountId(registerUserRequest.accountId());
        if (user.isPresent()) {
            throw new AccountIdAlreadyExistsException("중복된 이메일입니다");
        }
        if (!passwordVerify(registerUserRequest.accountPw())) {
            throw new PasswordVerifyException("비밀번호는 6자리 이상, 영문, 숫자, 특수문자 조합이어야 합니다.");
        }

        String encodedPassword = passwordEncoder.encode(registerUserRequest.accountPw());

        User registered_user = User.builder()
                .nickname(registerUserRequest.nickname())
                .accountId(registerUserRequest.accountId())
                .accountPw(encodedPassword)
                .roles(roles)
                .build();

        return userRepository.save(registered_user);
    }

    public static boolean passwordVerify(String password) {
        // 6자리 이상, 영문, 숫자, 특수문자 조합
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{6,}$");
    }

    public UserInfoResponse getUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AccountIdNotFoundException("사용자를 찾을 수 없습니다."));
        return userMapper.toDto(user);
    }

    public User getUser(String accountId) {
        return userRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AccountIdNotFoundException(accountId + " 사용자를 찾을 수 없습니다."));
    }

    public User grantedAdminPermission(String accountId) {
        User user = getUser(accountId);
        Set<UserRole> roles = user.getRoles();
        roles.add(UserRole.ADMIN);

        user.setRoles(roles);
        return userRepository.save(user);
    }

    public User confiscatedAdminPermission(String accountId) {
        User user = getUser(accountId);
        Set<UserRole> roles = user.getRoles();
        roles.remove(UserRole.ADMIN);

        user.setRoles(roles);
        return userRepository.save(user);
    }
}
