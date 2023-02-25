package com.developlife.reviewtwits.user;

import com.developlife.reviewtwits.message.request.LoginUserRequest;
import com.developlife.reviewtwits.message.request.RegisterUserRequest;
import com.developlife.reviewtwits.type.UserRole;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Set;

public class UserSteps {
    final static String nickname = "test";
    final static String accountId = "test@naver.com";
    final static String accountPw = "test1122!";

    public static ExtractableResponse<Response> 회원가입요청(final RegisterUserRequest request) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/user")
                .then()
                .log().all().extract();
    }

    public static RegisterUserRequest 회원가입요청_생성() {
        return RegisterUserRequest.builder().
                nickname(nickname)
                .accountId(accountId)
                .accountPw(accountPw)
                .build();
    }

    public static RegisterUserRequest 회원가입요청_어드민_생성() {
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

    public static RegisterUserRequest 회원가입요청_비밀번호규칙_불일치() {
        return RegisterUserRequest.builder()
                .accountId("pw_rule_failed_" + accountId)
                .accountPw("1112@@")
                .nickname("pw_rule_failed_" + nickname)
                .build();
    }
}
