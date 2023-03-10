package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.EmailVerify;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.mail.VerifyCodeException;
import com.developlife.reviewtwits.exception.user.*;
import com.developlife.reviewtwits.mapper.UserMapper;
import com.developlife.reviewtwits.message.request.user.LoginUserRequest;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.message.response.user.UserDetailInfoResponse;
import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import com.developlife.reviewtwits.repository.EmailVerifyRepository;
import com.developlife.reviewtwits.repository.RefreshTokenRepository;
import com.developlife.reviewtwits.repository.UserRepository;
import com.developlife.reviewtwits.type.UserRole;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, EmailVerifyRepository emailVerifyRepository, RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.emailVerifyRepository = emailVerifyRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public User login(LoginUserRequest loginUserRequest) {
        User user = getUser(loginUserRequest.accountId());
        if (!passwordEncoder.matches(loginUserRequest.accountPw(), user.getAccountPw())) {
            throw new AccountPasswordWrongException("??????????????? ???????????? ????????????.");
        }

        return user;
    }

    public User register(RegisterUserRequest registerUserRequest, Set<UserRole> roles) {
        Optional<User> user = userRepository.findByAccountId(registerUserRequest.accountId());
        if (user.isPresent()) {
            throw new AccountIdAlreadyExistsException("????????? ??????????????????");
        }
        if (!passwordVerify(registerUserRequest.accountPw())) {
            throw new PasswordVerifyException("??????????????? 6?????? ??????, ??????, ??????, ???????????? ??????????????? ?????????.");
        }
        authenticationCodeVerify(registerUserRequest.accountId(), registerUserRequest.verifyCode());


        String encodedPassword = passwordEncoder.encode(registerUserRequest.accountPw());

        User registered_user = userMapper.toUser(registerUserRequest);
        registered_user.setAccountPw(encodedPassword);
        registered_user.setRoles(roles);

        return userRepository.save(registered_user);
    }

    private void authenticationCodeVerify(String accountId, String verifyCode) {
        EmailVerify emailVerify = emailVerifyRepository.findByEmail(accountId)
                .orElseThrow(() -> new VerifyCodeException("???????????? ????????? ??????????????????"));
        LocalDateTime expiredDate = emailVerify.getCreatedDate().plusHours(1);
        if(LocalDateTime.now().isAfter(expiredDate)) {
            throw new VerifyCodeException("??????????????? ?????????????????????.");
        }
        if(emailVerify.isAlreadyUsed()) {
            throw new VerifyCodeException("?????? ????????? ?????????????????????.");
        }
        if(emailVerify.getVerifyCode().equals(verifyCode)) {
            emailVerify.setAlreadyUsed(true);
            emailVerifyRepository.save(emailVerify);
        } else {
            throw new VerifyCodeException("??????????????? ???????????? ????????????.");
        }
    }

    public static boolean passwordVerify(String password) {
        // 6?????? ??????, ??????, ??????, ???????????? ??????
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{6,}$");
    }

    public UserDetailInfoResponse getDetailUserInfo(String accountId) {
        User user = userRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AccountIdNotFoundException("???????????? ?????? ??? ????????????."));
        return userMapper.toUserDetailInfoResponse(user);
    }

    public UserInfoResponse getUserInfo(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserIdNotFoundException("???????????? ?????? ??? ????????????."));
        return userMapper.toUserInfoResponse(user);
    }

    private User getUser(String accountId) {
        return userRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AccountIdNotFoundException("???????????? ?????? ??????????????????."));
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

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(
                refreshTokenRepository::delete
        );
    }
}
