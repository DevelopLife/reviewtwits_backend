package com.developlife.reviewtwits.email;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.CommonDocument;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.service.UserService;
import com.developlife.reviewtwits.user.UserSteps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * @author ghdic
 * @since 2023/03/08
 */
public class EmailApiTest extends ApiTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserSteps userSteps;
    private RegisterUserRequest registerUserRequest;
    @BeforeEach
    void setting() {
        registerUserRequest = userSteps.회원가입정보_생성();
        // 일반유저 회원가입 해두고 테스트 진행
        userService.register(registerUserRequest, UserSteps.일반유저권한_생성());
    }

    @Test
    @DisplayName("아이디 찾기")
    void 아이디찾기_아이디존재_200() {
        var request = EmailSteps.아이디찾기_아이디존재_생성();

        given(this.spec)
            .filter(document(DEFAULT_RESTDOC_PATH, "개인정보로 아이디를 찾습니다. 아이디가 없을 경우 204를 반환합니다", "아이디찾기", EmailDocument.FindIdsEmailRequestField, EmailDocument.FindIdsEmailResponseField))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
        .when()
            .post("/emails/find-ids")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("find{it.accountId == '%s'}".formatted(UserSteps.accountId), notNullValue())
            .log().all().extract().response();
    }

    @Test
    @DisplayName("아이디 찾기 - 아이디 존재하지 않음")
    void 아이디찾기_아이디없음_204() {
        var request = EmailSteps.아이디찾기_아이디없음_생성();

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, EmailDocument.FindIdsEmailRequestField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/emails/find-ids")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all().extract().response();
    }

    @Test
    @DisplayName("비밀번호 찾기")
    void 비밀번호찾기_이메일보내기성공_200() {
        var request = EmailSteps.비밀번호찾기_이메일보내기성공_요청생성();

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "입력정보가 일치할 경우 accountId로 비밀번호 리셋 url이 담긴 이메일이 전송됩니다", "비밀번호 찾기", EmailDocument.FindIdsPasswrdRequestField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/emails/find-pw")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract().response();
    }
}
