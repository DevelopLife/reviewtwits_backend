package com.developlife.reviewtwits.project;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.CommonDocument;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.repository.ProjectRepository;
import com.developlife.reviewtwits.service.ProjectService;
import com.developlife.reviewtwits.service.user.UserService;
import com.developlife.reviewtwits.user.UserSteps;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;

/**
 * @author ghdic
 * @since 2023/03/09
 */
public class ProjectApiTest extends ApiTest {
    @Autowired
    UserService userService;
    @Autowired
    UserSteps userSteps;
    @Autowired
    ProjectService projectService;
    @Autowired
    ProjectRepository projectRepository;

    private RegisterUserRequest registerUserRequest;
    private RegisterUserRequest registerAdminRequest;
    private RegisterUserRequest registerOtherUserRequest;


    @BeforeEach
    void setting() {
        registerUserRequest = userSteps.회원가입정보_생성();
        registerAdminRequest = userSteps.회원가입정보_어드민_생성();
        registerOtherUserRequest = userSteps.상대유저_회원가입정보_생성();

        // 일반유저, 어드민유저 회원가입 해두고 테스트 진행
        userService.register(registerUserRequest, UserSteps.일반유저권한_생성());
        userService.register(registerAdminRequest, UserSteps.어드민유저권한_생성());
        userService.register(registerOtherUserRequest, UserSteps.일반유저권한_생성());
        User user = userService.getUser(UserSteps.accountId);

        IntStream.range(1, 3)
                .forEach(
                    i -> projectService.registerProject(ProjectSteps.프로젝트생성요청_생성(i), user));
    }


    @Test
    @DisplayName("프로젝트 생성")
    public void 프로젝트생성_성공_200() {
        final var request = ProjectSteps.프로젝트생성요청_생성(0);
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "프로젝트 생성을 요청하는 API 입니다." +
                                "<br>프로젝트 이름은 영어, 숫자, '-', '_' 4가지로만 작성할 수 있으며, 30자 이내로 작성해야 합니다." +
                                "<br>프로젝트 설명은 100 자 이내로 입력할 수 있습니다." +
                                "<br>URI Pattern 은 기존 HTTP URL 에서의 URI 식으로 입력해야 합니다." +
                                "<br>프로젝트 언어는 '한국어', 'ENGLISH' 두 개만 허용되어 있습니다." +
                                "<br>프로젝트 색상은 '#123456' 과 같이 입력해야 합니다." +
                                "<br>프로젝트 가격 플랜은 FREE_PLAN, PLUS_PLAN, PRO_PLAN, BUSINESS_PLAN 중 하나의 값으로 입력해야 합니다." +
                                "<br>헤더에 유저 토큰은 필수로 입력해야 합니다. 미입력 시 401 Unauthorized 가 반환됩니다." +
                                "<br>이미 존재하는 프로젝트 이름을 입력했을 경우, 409 Conflict 가 반환됩니다.", "프로젝트생성",
                        CommonDocument.AccessTokenHeader,
                        ProjectDocument.RegisterProjectRequestField,
                        ProjectDocument.ProjectInfoResponseField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(request)
                .when()
                .post("/projects")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getString("projectName")).isEqualTo(request.projectName());
        assertThat(jsonPath.getString("projectDescription")).isEqualTo(request.projectDescription());
        assertThat(jsonPath.getString("projectDescription")).isEqualTo(request.projectDescription());
        assertThat(jsonPath.getString("projectColor")).isEqualTo(request.projectColor());
        assertThat(jsonPath.getString("category")).isEqualTo(request.category());
    }

    @Test
    void 프로젝트생성_프로젝트명_영어숫자이외_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        final var wrongRequest = ProjectSteps.프로젝트생성요청_잘못된이름_생성(0);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(wrongRequest)
                .when()
                .post("/projects")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all().extract();
    }

    @Test
    void 프로젝트생성_URI_패턴_형식아님_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        final var wrongRequest = ProjectSteps.프로젝트생성요청_잘못된URI_생성(0);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(wrongRequest)
                .when()
                .post("/projects")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all().extract();
    }

    @Test
    void 프로젝트생성_헤더정보없음_401(){
        final var request = ProjectSteps.프로젝트생성요청_생성(0);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/projects")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all().extract();
    }

    @Test
    void 프로젝트생성_이미존재하는이름_409(){
        final var request = ProjectSteps.프로젝트생성요청_생성(1);
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/projects")
                .then()
                .assertThat()
                .statusCode(HttpStatus.CONFLICT.value())
                .log().all().extract();
    }

    @Test
    @DisplayName("프로젝트 리스트")
    public void 프로젝트리스트_프로젝트정보_200() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "유저의 프로젝트를 리스트를 반환합니다", "프로젝트리스트", CommonDocument.AccessTokenHeader, ProjectDocument.ProjectInfoListResponseField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .when()
                .get("/projects")
                .then()
                .assertThat()
//            .body("find{it.projectName == '%s".formatted(ProjectSteps.projectName) + "'}", notNullValue())
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getList("").size()).isEqualTo(2);
        assertThat(jsonPath.getString("[0].projectName")).contains(ProjectSteps.projectName);
        assertThat(jsonPath.getString("[1].projectName")).contains(ProjectSteps.projectName);
    }

    @Test
    @DisplayName("프로젝트 수정")
    public void 프로젝트수정_프로젝트정보_200() {
        final var request = ProjectSteps.프로젝트수정요청_생성();
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        final Long projectId = projectService.getProjectIdFromAccountId(UserSteps.accountId);

        given(this.spec)
            .filter(document(DEFAULT_RESTDOC_PATH, "프로젝트를 수정합니다", "프로젝트수정",
                CommonDocument.AccessTokenHeader, ProjectDocument.ProjectIdPathParam,
                ProjectDocument.FixProjectRequestField,
                ProjectDocument.ProjectSettingInfoResponseField))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("projectId", projectId)
            .header("X-AUTH-TOKEN", token)
            .body(request)
        .when()
            .patch("/projects/{projectId}")
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("projectName", equalTo(request.projectName()))
            .log().all();
    }
}
