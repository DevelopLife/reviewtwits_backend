package com.developlife.reviewtwits.user;

import com.developlife.reviewtwits.message.request.user.LoginUserRequest;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.type.Gender;
import com.developlife.reviewtwits.type.UserRole;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class UserSteps {
    final static String nickname = "test";
    final static String accountId = "test@naver.com";
    final static String accountPw = "test1122!";
    final static LocalDateTime birthday = LocalDateTime.now();
    final static String phoneNumber = "01012345678";
    final static Gender gender = Gender.남자;

    public static ExtractableResponse<Response> 회원가입요청(final RegisterUserRequest request) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/user")
                .then()
                .log().all().extract();
    }

    public static RegisterUserRequest 회원가입정보_생성() {
        return RegisterUserRequest.builder()
                .nickname(nickname)
                .accountId(accountId)
                .accountPw(accountPw)
                .birthday(birthday)
                .gender(gender)
                .phoneNumber(phoneNumber)
                .authenticationCode("123456")
                .build();
    }

    public static RegisterUserRequest 회원가입정보_어드민_생성() {
        return RegisterUserRequest.builder().
                nickname(nickname+"_admin")
                .accountId("admin_" + accountId)
                .accountPw(accountPw)
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

    public static ExtractableResponse<Response> 로그인요청(LoginUserRequest loginUserRequest) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginUserRequest)
                .when()
                .post("/user/login")
                .then()
                .log().all().extract();
    }

    public static ExtractableResponse<Response> 자신정보조회요청(String token) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .when()
                .get("/user/me")
                .then()
                .log().all().extract();
    }

    public static RegisterUserRequest 추가회원가입정보_생성() {
        return RegisterUserRequest.builder()
                .nickname(nickname)
                .accountId("add_" + accountId)
                .accountPw(accountPw)
                .birthday(birthday)
                .gender(gender)
                .phoneNumber("01011110000")
                .authenticationCode("123456")
                .build();
    }

    public static RegisterUserRequest 회원가입요청_입력정보_누락() {
        return RegisterUserRequest.builder()
                .gender(gender)
                .build();
    }

    public static RegisterUserRequest 회원가입요청_입력정보_부적합() {
        return RegisterUserRequest.builder()
                .nickname(nickname)
                .accountId("add_" + accountId)
                .accountPw("1234")
                .birthday(birthday)
                .gender(gender)
                .phoneNumber("전화번호")
                .authenticationCode("123456")
                .build();
    }

    public static RegisterUserRequest 회원가입요청_비밀번호규칙_불일치() {
        return RegisterUserRequest.builder()
                .nickname(nickname)
                .accountId(accountId)
                .accountPw("123@@@")
                .birthday(birthday)
                .gender(gender)
                .phoneNumber(phoneNumber)
                .authenticationCode("123456")
                .build();
    }
}
