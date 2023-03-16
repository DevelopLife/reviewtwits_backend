package com.developlife.reviewtwits.oauth;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.CommonDocument;
import com.developlife.reviewtwits.user.UserSteps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;

/**
 * @author ghdic
 * @since 2023/03/15
 */
public class OauthApiTest extends ApiTest {
    @Test
    @DisplayName("네이버 토큰 인증")
    void 네이버토큰_인증_200() {
        given(this.spec)
            .filter(document(DEFAULT_RESTDOC_PATH, "테스트를 진행할수없는 API입니다<br>200: 토큰이 유효하고 이미 회원가입이 되어있는 경우, 로그인과 동일하게 토큰 발급을 응답으로 줍니다<br>422: 회원가입 정보 입력필요, 에러메세지응답<br>401: 토큰이 유효하지않음, 에러메세지응답", "네이버 토큰으로 인증받아 로그인", CommonDocument.AuthorizationHeader))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", "발급 받은 토큰을 입력해주세요")
            .when()
            .post("/oauth/naver")
            .then()
            .log().all().extract().response();
    }

    @Test
    @DisplayName("카카오 토큰 인증")
    void 카카오토큰_인증_200() {
        given(this.spec)
            .filter(document(DEFAULT_RESTDOC_PATH, "테스트를 진행할수없는 API입니다<br>200: 토큰이 유효하고 이미 회원가입이 되어있는 경우, 로그인과 동일하게 토큰 발급을 응답으로 줍니다<br>422: 회원가입 정보 입력필요, 에러메세지응답<br>401: 토큰이 유효하지않음, 에러메세지응답", "카카오 토큰으로 인증받아 로그인", CommonDocument.AuthorizationHeader))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", "발급 받은 토큰을 입력해주세요")
            .when()
            .post("/oauth/kakao")
            .then()
            .log().all().extract().response();
    }

    @Test
    @DisplayName("구글 토큰 인증")
    void 구글토큰_인증_200() {
        given(this.spec)
            .filter(document(DEFAULT_RESTDOC_PATH, "테스트를 진행할수없는 API입니다<br>200: 토큰이 유효하고 이미 회원가입이 되어있는 경우, 로그인과 동일하게 토큰 발급을 응답으로 줍니다<br>422: 회원가입 정보 입력필요, 에러메세지응답<br>401: 토큰이 유효하지않음, 에러메세지응답", "구글 토큰으로 인증받아 로그인", CommonDocument.AuthorizationHeader))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", "발급 받은 토큰을 입력해주세요")
            .when()
            .post("/oauth/google")
            .then()
            .log().all().extract().response();
    }

    @Test
    @DisplayName("회원 가입 추가정보 입력")
    void 회원가입_추가정보입력_200() {
        var request = OauthSteps.회원가입추가정보생성();

        given(this.spec)
            .filter(document(DEFAULT_RESTDOC_PATH, "테스트를 진행할수없는 API입니다" +
                "<br>200: 토큰이 유효하고 정상적인 회원가입 정보가 기입된 경우, 로그인과 동일하게 토큰 발급을 응답으로 줍니다<br>" +
                "400: 올바르지않은 회원가입 정보가 기입되거나 이미 입력한 회원가입 정보가 존재한 경우, 에러메세지응답<br>" +
                "401: 토큰이 유효하지않거나 아이디를 찾지못함(토큰 인증 요청을 거친적이 없음, 비정상경로), 에러메세지응답",
                "회원가입 추가정보 입력", CommonDocument.AuthorizationHeader, OauthDocument.RegisterOauthUserRequestField))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", "발급 받은 토큰을 입력해주세요")
            .body(request)
            .when()
            .post("/oauth/register")
            .then()
            .log().all().extract().response();
    }
}
