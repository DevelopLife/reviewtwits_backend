package com.developlife.reviewtwits.user;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.controller.UserController;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.user.LoginUserRequest;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.message.response.user.JwtTokenResponse;
import com.developlife.reviewtwits.repository.UserRepository;
import com.developlife.reviewtwits.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserApiTest extends ApiTest {

    @Autowired
    private UserController userController;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private final RegisterUserRequest registerUserRequest = UserSteps.회원가입정보_생성();
    private final RegisterUserRequest registerAdminRequest = UserSteps.회원가입정보_어드민_생성();



    @BeforeEach
    void setting() {
        // 일반유저, 어드민유저 회원가입 해두고 테스트 진행
        userService.register(registerUserRequest, UserSteps.일반유저권한_생성());
        userService.register(registerAdminRequest, UserSteps.어드민유저권한_생성());
    }

    @Test
    @DisplayName("특정유저조회")
    void 특정유저조회_유저정보확인_True() {
        User user = userRepository.findByAccountId(registerUserRequest.accountId()).get();

        final var response = UserSteps.특정유저조회요청(user.getUserId());

        // 기본 정보 표시
        assertThat(response.jsonPath().getString("nickname")).isEqualTo(registerUserRequest.nickname());
        // 민감정보 노출x
        assertThat(response.jsonPath().getString("phoneNumber")).isNull();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("자신정보조회")
    void 자신정보조회_유저정보확인_True() throws JsonProcessingException {
        final String token = 로그인토큰정보().accessToken();
        final var response = UserSteps.자신정보조회요청(token);

        // 기본 정보 표시
        assertThat(response.jsonPath().getString("nickname")).isEqualTo(registerUserRequest.nickname());
        // 민감정보 노출o
        assertThat(response.jsonPath().getString("phoneNumber")).isEqualTo(registerUserRequest.phoneNumber());
        // 비밀번호는 예외
        assertThat(response.jsonPath().getString("password")).isNull();
    }

    @Test
    @DisplayName("로그인성공")
    void 로그인성공_로그인정보확인_True() throws JsonProcessingException {
        final JwtTokenResponse response = 로그인토큰정보();

        assertThat(response.accessToken()).isNotNull();
    }

    @Test
    @DisplayName("로그인실패")
    void 로그인실패_로그인정보불일치_False() {
        // 아이디가 존재하지 않음
        // 비밀번호 불일치

    }

    @Test
    @DisplayName("회원가입 성공")
    void 회원가입체크_회원가입정보저장확인_True() {
//        final var request = UserSteps.회원가입정보_생성();
//
//        final var response = UserSteps.회원가입요청(request);
//
//        assertThat(response.jsonPath().getString("userId")).isEqualTo("1");
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("회원가입 실패 - 입력정보부족")
    void 회원가입체크_입력정보부족_False() {

    }

    @Test
    @DisplayName("회원가입 실패 - 입력한 정보가 조건에 맞지않음")
    void 회원가입체크_입력조건부적합_False() {
        // 대소문자 구분
        // 6글자 이상
        // 알파벳, 숫자, 특수문자 1개이상 들어가있는지

    }

    @Test
    @DisplayName("유저 권한 확인")
    void 유저권한확인_유저권학부여확인_True() {

    }

    @Test
    @DisplayName("JWT토큰생성확인")
    void JWT토큰생성확인_토큰존재여부_True() {

    }

    @Test
    @DisplayName("JWT토큰인증확인")
    void JWT토큰인증확인_인증여부_True() {

    }

    @Test
    @DisplayName("토큰제공자별 응답확인")
    void 토큰제공자별응답확인_응답확인_True() {

    }

    @Test
    @DisplayName("이메일 인증번호 발급")
    void 이메일인증번호발급_인증번호발급확인_True() {
        // 따로 테스트 불가능 직접 확인
    }

    JwtTokenResponse 로그인토큰정보() throws JsonProcessingException {
        final LoginUserRequest loginUserRequest = UserSteps.로그인요청생성();
        final var loginResponse = UserSteps.로그인요청(loginUserRequest);
        JwtTokenResponse jwtTokenResponse =  objectMapper.readValue(loginResponse.body().asString(), JwtTokenResponse.class);
        // groovy에서 파싱을 못해서 에러남
        // final JwtTokenResponse jwtTokenResponse = loginResponse.body().as(JwtTokenResponse.class);
        return jwtTokenResponse;
    }
}
