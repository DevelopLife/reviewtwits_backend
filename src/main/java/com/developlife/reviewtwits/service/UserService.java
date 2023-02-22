package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.AccountIdAlreadyExistsException;
import com.developlife.reviewtwits.exception.AccountPasswordWrongException;
import com.developlife.reviewtwits.message.request.LoginUserRequest;
import com.developlife.reviewtwits.message.request.RegisterUserRequest;
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

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User login(LoginUserRequest loginUserRequest) {
        User user = userRepository.findByAccountId(loginUserRequest.accountId())
                .orElseThrow(() -> new AccountIdAlreadyExistsException("사용자를 찾을 수 없습니다."));
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
        String encodedPassword = passwordEncoder.encode(registerUserRequest.accountPw());

        User registered_user = User.builder()
                .username(registerUserRequest.username())
                .accountId(registerUserRequest.accountId())
                .accountPw(encodedPassword)
                .roles(roles)
                .build();

        return userRepository.save(registered_user);
    }

    public User getUser(String accountId) {
        System.out.println(accountId);
        Optional<User> user = userRepository.findByAccountId(accountId);
        return user.get();
    }

    public User grantedAdminPermission(String accountId) {
        User user = userRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AccountIdAlreadyExistsException("사용자를 찾을 수 없습니다."));
        Set<UserRole> roles = user.getRoles();
        roles.add(UserRole.ADMIN);

        user.setRoles(roles);
        return userRepository.save(user);
    }

    public User confiscatedAdminPermission(String accountId) {
        User user = userRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AccountIdAlreadyExistsException("사용자를 찾을 수 없습니다."));
        Set<UserRole> roles = user.getRoles();
        roles.remove(UserRole.ADMIN);

        user.setRoles(roles);
        return userRepository.save(user);
    }
}
