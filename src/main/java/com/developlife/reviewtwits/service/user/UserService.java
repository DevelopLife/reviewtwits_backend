package com.developlife.reviewtwits.service.user;

import com.developlife.reviewtwits.config.security.JwtTokenProvider;
import com.developlife.reviewtwits.entity.EmailVerify;
import com.developlife.reviewtwits.entity.RefreshToken;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.user.PhoneNumberAlreadyExistsException;
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
import com.developlife.reviewtwits.type.JwtCode;
import com.developlife.reviewtwits.type.JwtProvider;
import com.developlife.reviewtwits.type.UserRole;
import io.jsonwebtoken.Jwts;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
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
            throw new AccountPasswordWrongException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    public User register(RegisterUserRequest registerUserRequest, Set<UserRole> roles) {
        userRepository.findByAccountId(registerUserRequest.accountId()).ifPresent(
            user -> {throw new AccountIdAlreadyExistsException("중복된 이메일입니다");}
        );

        userRepository.findByAccountIdOrPhoneNumber(registerUserRequest.accountId(),
            registerUserRequest.phoneNumber()).ifPresent(
            user -> {
                if (user.getAccountId().equals(registerUserRequest.accountId())) {
                    throw new AccountIdAlreadyExistsException("중복된 이메일입니다");
                } else {
                    throw new PhoneNumberAlreadyExistsException("중복된 전화번호입니다");
                }
            });

        if (!passwordVerify(registerUserRequest.accountPw())) {
            throw new PasswordVerifyException("비밀번호는 6자리 이상, 영문, 숫자, 특수문자 조합이어야 합니다.");
        }
        authenticationCodeVerify(registerUserRequest.accountId(), registerUserRequest.verifyCode());


        String encodedPassword = passwordEncoder.encode(registerUserRequest.accountPw());

        User registeredUser = userMapper.toUser(registerUserRequest);
        registeredUser.setAccountPw(encodedPassword);
        registeredUser.setRoles(roles);
        registeredUser.setProvider(JwtProvider.REVIEWTWITS);

        return userRepository.save(registeredUser);
    }

    private void authenticationCodeVerify(String accountId, String verifyCode) {
        EmailVerify emailVerify = emailVerifyRepository.findByEmail(accountId)
                .orElseThrow(() -> new VerifyCodeException("인증코드 발급을 진행해주세요"));
        LocalDateTime expiredDate = emailVerify.getCreatedDate().plusHours(1);
        if(LocalDateTime.now().isAfter(expiredDate)) {
            throw new VerifyCodeException("인증코드가 만료되었습니다.");
        }
        if(emailVerify.isAlreadyUsed()) {
            throw new VerifyCodeException("이미 사용된 인증코드입니다.");
        }
        if(emailVerify.getVerifyCode().equals(verifyCode)) {
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

    public UserDetailInfoResponse getDetailUserInfo(User user) {
        if(user == null){
            throw new AccountIdNotFoundException("사용자를 찾을 수 없습니다.");
        }
        return userMapper.toUserDetailInfoResponse(user);
    }

    public UserInfoResponse getUserInfo(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserIdNotFoundException("사용자를 찾을 수 없습니다."));
        return userMapper.toUserInfoResponse(user);
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

    @Transactional
    public void logout(User user) {
        refreshTokenRepository.findByAccountId(user.getAccountId()).ifPresent(
                refreshTokenRepository::delete
        );
    }
}
