package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.EmailVerify;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.user.*;
import com.developlife.reviewtwits.mapper.UserMapper;
import com.developlife.reviewtwits.message.request.user.LoginUserRequest;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.message.response.user.UserDetailInfoResponse;
import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import com.developlife.reviewtwits.repository.EmailVerifyRepository;
import com.developlife.reviewtwits.repository.UserRepository;
import com.developlife.reviewtwits.type.UserRole;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final EmailVerifyRepository emailVerifyRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, EmailVerifyRepository emailVerifyRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.emailVerifyRepository = emailVerifyRepository;
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
        authenticationCodeVerify(registerUserRequest.accountId(), registerUserRequest.authenticationCode());


        String encodedPassword = passwordEncoder.encode(registerUserRequest.accountPw());

        User registered_user = userMapper.toEntity(registerUserRequest);
        registered_user.setAccountPw(encodedPassword);
        registered_user.setRoles(roles);

        return userRepository.save(registered_user);
    }

    private void authenticationCodeVerify(String accountId, String authenticationCode) {
        EmailVerify emailVerify = emailVerifyRepository.findByEmail(accountId)
                .orElseThrow(() -> new VerifyCodeException("인증코드 발급을 진행해주세요"));
        LocalDateTime expiredDate = emailVerify.getCreateDate().plusHours(1);
        if(LocalDateTime.now().isAfter(expiredDate)) {
            throw new VerifyCodeException("인증코드가 만료되었습니다.");
        }
        if(emailVerify.isAlreadyUsed()) {
            throw new VerifyCodeException("이미 사용된 인증코드입니다.");
        }
        if(emailVerify.getVerifyCode().equals(authenticationCode)) {
            emailVerify.setAlreadyUsed(true);
            emailVerifyRepository.save(emailVerify);
        } else {
            throw new VerifyCodeException("인증코드가 일치하지 않습니다.");
        }
    }

    public static boolean passwordVerify(String password) {
        // 6자리 이상, 영문, 숫자, 특수문자 조합
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{6,}$");
    }

    public UserDetailInfoResponse getDetailUserInfo(String accountId) {
        User user = userRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AccountIdNotFoundException("사용자를 찾을 수 없습니다."));
        return userMapper.toDTO(user);
    }

    public UserInfoResponse getUserInfo(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserIdNotFoundException("사용자를 찾을 수 없습니다."));
        return userMapper.toDto(user);
    }

    private User getUser(String accountId) {
        return userRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AccountIdNotFoundException("존재하지 않는 아이디입니다."));
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
