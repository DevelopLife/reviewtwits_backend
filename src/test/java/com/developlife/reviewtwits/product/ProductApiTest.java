package com.developlife.reviewtwits.product;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.CommonDocument;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.project.ProjectDocument;
import com.developlife.reviewtwits.project.ProjectSteps;
import com.developlife.reviewtwits.service.ProjectService;
import com.developlife.reviewtwits.service.user.UserService;
import com.developlife.reviewtwits.user.UserDocument;
import com.developlife.reviewtwits.user.UserSteps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static io.restassured.RestAssured.given;

/**
 * @author WhalesBob
 * @since 2023-05-02
 */
public class ProductApiTest extends ApiTest {

    @Autowired
    UserService userService;
    @Autowired
    UserSteps userSteps;
    @Autowired
    ProjectService projectService;

    private RegisterUserRequest registerUserRequest;
    private RegisterUserRequest registerOtherUserRequest;
    private long projectId;

    @BeforeEach
    void setting() {
        registerUserRequest = userSteps.회원가입정보_생성();
        registerOtherUserRequest = userSteps.상대유저_회원가입정보_생성();

        // 일반유저, 어드민유저 회원가입 해두고 테스트 진행
        userService.register(registerUserRequest, UserSteps.일반유저권한_생성());
        userService.register(registerOtherUserRequest, UserSteps.일반유저권한_생성());
        User user = userService.getUser(UserSteps.accountId);

        String projectIdString = projectService.registerProject(ProjectSteps.프로젝트생성요청_생성(), user).projectId();
        projectId = Long.parseLong(projectIdString);
    }

    @Test
    void 제품_URL_등록_성공_200(){

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "임시로 사용할, 제품 URL 등록 API 입니다." +
                        "<br>X-AUTH-TOKEN 헤더가 없다면, 401 Unauthorized 가 반환됩니다." +
                        "<br>요청한 유저가 프로젝트에 대한 권한을 가지고 있지 않다면, 403 Forbidden 이 반환됩니다." +
                        "<br>입력한 프로젝트 이름으로 된 프로젝트가 존재하지 않는다면, 404 Not Found 가 반환됩니다." +
                        "<br>입력한 제품 URL 이 올바른 양식이 아니라면, 400 Bad Request 가 반환됩니다." +
                        "<br>입력한 프로젝트 아이디가 양수가 아니라면, 400 Bad Request 가 반환됩니다.", "제품URL등록API",
                        UserDocument.AccessTokenHeader,
                        ProjectDocument.ProjectNamePathParam,
                        ProductDocument.ProductUrlRegisterRequestFields,
                        ProductDocument.ProductRegisterResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN",token)
                .pathParam("projectName", ProjectSteps.projectName)
                .body(ProductSteps.제품URL등록요청_생성())
                .when()
                .post("/products/register/{projectName}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();
    }

    @Test
    void 제품_URL_등록_유저헤더정보없음_401(){

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("projectName", ProjectSteps.projectName)
                .body(ProductSteps.제품URL등록요청_생성())
                .when()
                .post("/products/register/{projectName}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all().extract();
    }

    @Test
    void 제품_URL_등록_프로젝트_권한없음_403(){

        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN",otherToken)
                .pathParam("projectName",ProjectSteps.projectName)
                .body(ProductSteps.제품URL등록요청_생성())
                .when()
                .post("/products/register/{projectName}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all().extract();
    }

    @Test
    void 제품_URL_등록_프로젝트_없음_404(){
        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN",otherToken)
                .pathParam("projectName",ProjectSteps.wrongProjectName)
                .body(ProductSteps.제품URL등록요청_생성())
                .when()
                .post("/products/register/{projectName}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all().extract();
    }
    @Test
    void 제품_URL_등록_URL_양식_오류_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN",token)
                .pathParam("projectName",ProjectSteps.projectName)
                .body(ProductSteps.제품URL_URL양식_아님())
                .when()
                .post("/products/register/{projectName}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all().extract();
    }

    @Test
    void 제품_URL_등록_제품이름_누락_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN",token)
                .pathParam("projectName",ProjectSteps.projectName)
                .body(ProductSteps.제품URL_제품이름_누락())
                .when()
                .post("/products/register/{projectName}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all().extract();
    }
}