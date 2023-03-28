package com.developlife.reviewtwits.user;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.user.AccountIdAlreadyExistsException;
import com.developlife.reviewtwits.exception.user.AccountIdNotFoundException;
import com.developlife.reviewtwits.exception.user.AccountPasswordWrongException;
import com.developlife.reviewtwits.exception.user.PasswordVerifyException;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.repository.UserRepository;
import com.developlife.reviewtwits.service.email.EmailService;
import com.developlife.reviewtwits.service.user.UserService;
import com.developlife.reviewtwits.type.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author ghdic
 * @since 2023/02/24
 */
public class UserServiceTest extends ApiTest {

    private final UserSteps userSteps;
    private final UserService userService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private RegisterUserRequest registerUserRequest;
    private RegisterUserRequest registerAdminRequest;
    @Autowired
    public UserServiceTest(UserSteps userSteps, UserService userService, UserRepository userRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userSteps = userSteps;
        this.userService = userService;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeEach
    void setting() {
        registerUserRequest = userSteps.회원가입정보_생성();
        registerAdminRequest = userSteps.회원가입정보_어드민_생성();
        // 일반유저, 어드민유저 회원가입 해두고 테스트 진행
        userService.register(registerUserRequest, UserSteps.일반유저권한_생성());
        userService.register(registerAdminRequest, UserSteps.어드민유저권한_생성());
    }

    @Test
    void 회원등록_성공() {
        final User user = userRepository.findByAccountId(registerUserRequest.accountId()).get();

        // 입력한 정보로 가입한 유저가 없는 경우 확인
        assertThat(user).isNotNull();
        // 입력정보 제대로 들어갔나 확인
        assertThat(registerUserRequest.accountId().equals(user.getAccountId())).isTrue();
        // 비밀번호 해시화 확인
        assertThat(passwordEncoder.matches(registerUserRequest.accountPw(), user.getAccountPw())).isTrue();
    }

    @Test
    void 회원등록_실패() {
        assertThrows(AccountIdAlreadyExistsException.class, () -> {
            userService.register(registerUserRequest, UserSteps.일반유저권한_생성());
        });

        assertThrows(PasswordVerifyException.class, () -> {
            userService.register(UserSteps.회원가입요청_비밀번호규칙_불일치(), UserSteps.일반유저권한_생성());
        });
    }

    @Test
    void 비밀번호규칙확인() {
        // 비밀번호 규칙: 6자 이상 1개 이상의 알파벳, 1개 이상의 숫자, 1개 이상의 특수문자 @$!%*#?&
        // 비밀번호 규칙 확인 - 성공
        UserSteps.규칙이맞는비밀번호들().stream().forEach(password -> {
            assertThat(UserService.passwordVerify(password)).isTrue();
        });
        // 비밀번호 규칙 확인 - 알파벳 x
        // 비밀번호 규칙 확인 - 숫자 x
        // 비밀번호 규칙 확인 - 특수문자 x
        // 비밀번호 규칙 확인 - 알파벳 대소문자 구분
        // 비밀번호 규칙 확인 - 비밀번호 길이 경계테스트
        UserSteps.규칙이틀린비밀번호들().stream().forEach(password -> {
            assertThat(UserService.passwordVerify(password)).isFalse();
        });
    }

    @Test
    void 로그인() {
        final User user_success = userService.login(UserSteps.로그인요청_생성_성공());

        // 로그인 성공
        assertThat(user_success).isNotNull();
        // 로그인 실패 - 비밀번호 불일치
        assertThrows(AccountPasswordWrongException.class, () -> {
            userService.login(UserSteps.로그인요청_생성_비밀번호불일치());
        });
        // 로그인 실패 - 아이디 존재x
        assertThrows(AccountIdNotFoundException.class, () -> {
            userService.login(UserSteps.로그인요청_생성_아이디불일치());
        });
    }

    @Test
    void 권한부여() {
        userService.grantedAdminPermission(registerUserRequest.accountId());
        final User user = userRepository.findByAccountId(registerUserRequest.accountId()).get();
        assertThat(user.getRoles().contains(UserRole.ADMIN)).isTrue();
    }

    @Test
    void 권한압수() {
        userService.confiscatedAdminPermission(registerAdminRequest.accountId());
        final User user = userRepository.findByAccountId(registerUserRequest.accountId()).get();
        assertThat(user.getRoles().contains(UserRole.ADMIN)).isFalse();
    }
}
