package com.developlife.reviewtwits.user;

import com.developlife.reviewtwits.message.request.user.LoginUserRequest;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.message.response.user.JwtTokenResponse;
import com.developlife.reviewtwits.service.email.EmailCodeSender;
import com.developlife.reviewtwits.type.EmailType;
import com.developlife.reviewtwits.type.Gender;
import com.developlife.reviewtwits.type.UserRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class UserSteps {
    final static String nickname = "templates";
    public final static String accountId = "test@naver.com";
    final static String accountPw = "test1122!";
    public final static String birthDate = "2002-01-01";
    public final static String phoneNumber = "01012345678";
    final static Gender gender = Gender.남자;

    final static String targetAccountId = "whalesbob@naver.com";

    final private EmailCodeSender emailSender;
    final private ObjectMapper objectMapper;

    public UserSteps(EmailCodeSender emailSender, ObjectMapper objectMapper) {
        this.emailSender = emailSender;
        this.objectMapper = objectMapper;
    }

    public static ExtractableResponse<Response> 회원가입요청(final RegisterUserRequest request) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/user")
                .then()
                .log().all().extract();
    }

    public RegisterUserRequest 회원가입정보_생성() {
        String key = emailSender.storageVerifyInfo(accountId, EmailType.회원가입인증코드);

        return RegisterUserRequest.builder()
                .nickname(nickname)
                .accountId(accountId)
                .accountPw(accountPw)
                .birthDate(birthDate)
                .gender(gender.toString())
                .phoneNumber(phoneNumber)
                .verifyCode(key)
                .build();
    }

    public RegisterUserRequest 팔로우상대방_회원가입정보_생성(){


        String targetNickname = "whalesbob";
        String targetBirthDate = "1996-11-22";
        String targetPhoneNumber = "01011112222";

        String key = emailSender.storageVerifyInfo(targetAccountId, EmailType.회원가입인증코드);

        return RegisterUserRequest.builder()
                .nickname(targetNickname)
                .accountId(targetAccountId)
                .accountPw(accountPw)
                .birthDate(targetBirthDate)
                .gender(gender.toString())
                .phoneNumber(targetPhoneNumber)
                .verifyCode(key)
                .build();
    }

    public RegisterUserRequest 회원가입정보_어드민_생성() {
        String key = emailSender.storageVerifyInfo("admin_" + accountId, EmailType.회원가입인증코드);

        return RegisterUserRequest.builder().
                nickname(nickname+"_admin")
                .accountId("admin_" + accountId)
                .accountPw(accountPw)
                .birthDate(birthDate)
                .gender(gender.toString())
                .phoneNumber("01099999999")
                .verifyCode(key)
                .build();
    }

    public static Set<UserRole> 일반유저권한_생성() {
        return Set.of(UserRole.USER);
    }

    public static Set<UserRole> 어드민유저권한_생성() {
        return Set.of(UserRole.USER, UserRole.ADMIN);
    }

    public static LoginUserRequest 로그인요청_생성_성공() {
        return LoginUserRequest.builder()
                .accountId(accountId)
                .accountPw(accountPw)
                .build();
    }

    public static LoginUserRequest 로그인요청_생성_비밀번호불일치() {
        return LoginUserRequest.builder()
                .accountId(accountId)
                .accountPw("wrongPassword")
                .build();
    }

    public static LoginUserRequest 로그인요청_생성_아이디불일치() {
        return LoginUserRequest.builder()
                .accountId("wrongId@test.com")
                .accountPw("wrongPassword")
                .build();
    }

    public static List<String> 규칙이맞는비밀번호들() {
        return List.of("test13!", "!te!s1t", "a213233123@12323432525",
                "a1@$!%*#?&");
    }

    public static List<String> 규칙이틀린비밀번호들() {
        // 비밀번호 규칙 확인 - 알파벳 x
        // 비밀번호 규칙 확인 - 숫자 x
        // 비밀번호 규칙 확인 - 특수문자 x
        // 비밀번호 규칙 확인 - 알파벳 대소문자 구분
        // 비밀번호 규칙 확인 - 비밀번호 길이 경계테스트
        return List.of("1231231@@",
                "testtest@@",
                "test123123",
                "TEST123123",
                "a1@a3", "a1@");
    }

    public static ExtractableResponse<Response> 특정유저조회요청(final long userId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("userId", userId)
                .when()
                .get("/user/info/{userId}")
                .then()
                .log().all().extract();
    }

    public static LoginUserRequest 로그인요청생성() {
        return LoginUserRequest.builder()
                .accountId(accountId)
                .accountPw(accountPw)
                .build();
    }

    public static LoginUserRequest 팔로우상대_로그인요청생성(){
        return LoginUserRequest.builder()
                .accountId(targetAccountId)
                .accountPw(accountPw)
                .build();
    }

    public static ExtractableResponse<Response> 로그인요청(LoginUserRequest loginUserRequest) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginUserRequest)
                .when()
                .post("/users/login")
                .then()
                .log().all().extract();
    }

    public RegisterUserRequest 추가회원가입정보_생성() {
        String key = emailSender.storageVerifyInfo("add_" + accountId, EmailType.회원가입인증코드);

        return RegisterUserRequest.builder()
                .nickname("add_" + nickname)
                .accountId("add_" + accountId)
                .accountPw(accountPw)
                .birthDate(birthDate)
                .gender(gender.toString())
                .phoneNumber("01011110000")
                .verifyCode(key)
                .build();
    }

    public static RegisterUserRequest 회원가입요청_입력정보_누락() {
        return RegisterUserRequest.builder()
                .gender(gender.toString())
                .build();
    }

    public static RegisterUserRequest 회원가입요청_입력정보_부적합() {
        return RegisterUserRequest.builder()
                .nickname(nickname)
                .accountId("add_" + accountId)
                .accountPw("1234")
                .birthDate(birthDate)
                .gender(gender.toString())
                .phoneNumber("전화번호")
                .verifyCode("123456")
                .build();
    }

    public static RegisterUserRequest 회원가입요청_비밀번호규칙_불일치() {
        return RegisterUserRequest.builder()
                .nickname(nickname)
                .accountId("wrong@test.com")
                .accountPw("123@@@")
                .birthDate(birthDate)
                .gender(gender.toString())
                .phoneNumber("01099998888")
                .verifyCode("123456")
                .build();
    }

    public String 로그인액세스토큰정보(LoginUserRequest request) {
        final var loginResponse = UserSteps.로그인요청(request);
        return loginResponse.body().jsonPath().getString("accessToken");
//        try {
//            JwtTokenResponse jwtTokenResponse = objectMapper.readValue(loginResponse.body().asString(), JwtTokenResponse.class);
//
//            // groovy에서 파싱을 못해서 에러남
//            // final JwtTokenResponse jwtTokenResponse = loginResponse.body().as(JwtTokenResponse.class);
//            return jwtTokenResponse.accessToken();
//        } catch (JsonProcessingException e) {
//            return "";
//        }
    }

    public String 로그인리프래시토큰정보(LoginUserRequest request) {
        final var loginResponse = UserSteps.로그인요청(request);
        return loginResponse.cookie("refreshToken");
    }
}
