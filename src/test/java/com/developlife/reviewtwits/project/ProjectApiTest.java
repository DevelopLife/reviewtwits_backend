package com.developlife.reviewtwits.project;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.service.ProjectService;
import com.developlife.reviewtwits.service.UserService;
import com.developlife.reviewtwits.user.UserDocument;
import com.developlife.reviewtwits.user.UserSteps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
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

    private RegisterUserRequest registerUserRequest;
    private RegisterUserRequest registerAdminRequest;


    @BeforeEach
    void setting() {
        registerUserRequest = userSteps.회원가입정보_생성();
        registerAdminRequest = userSteps.회원가입정보_어드민_생성();

        // 일반유저, 어드민유저 회원가입 해두고 테스트 진행
        userService.register(registerUserRequest, UserSteps.일반유저권한_생성());
        userService.register(registerAdminRequest, UserSteps.어드민유저권한_생성());

        IntStream.range(1, 3)
                .forEach(
                    i -> projectService.registerProject(ProjectSteps.프로젝트생성요청_생성(), UserSteps.accountId));
    }



    @Test
    @DisplayName("프로젝트 생성")
    public void 프로젝트생성_프로젝트정보_200() {
        final var request = ProjectSteps.프로젝트생성요청_생성();
        final String token = userSteps.로그인토큰정보(UserSteps.로그인요청생성()).accessToken();

        given(this.spec)
            .filter(document(DEFAULT_RESTDOC_PATH, "프로젝트를 생성합니다", "프로젝트생성", UserDocument.AccessTokenHeader, ProjectDocument.RegisterProjectRequestField))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("X-AUTH-TOKEN", token)
            .body(request)
        .when()
            .post("/projects")
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .log().all();
    }

    @Test
    @DisplayName("프로젝트 리스트")
    public void 프로젝트리스트_프로젝트정보_200() {
        final String token = userSteps.로그인토큰정보(UserSteps.로그인요청생성()).accessToken();

        given(this.spec)
            .filter(document(DEFAULT_RESTDOC_PATH, "프로젝트를 생성합니다", "프로젝트생성", UserDocument.AccessTokenHeader, ProjectDocument.ProjectInfoListResponseField))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("X-AUTH-TOKEN", token)
            .when()
            .get("/projects")
            .then()
            .assertThat()
            .body("find{it.projectName == '%s".formatted(ProjectSteps.projectName) + "'}", notNullValue())
            .statusCode(HttpStatus.OK.value())
            .log().all();
    }

    @Test
    @DisplayName("프로젝트 수정")
    public void 프로젝트수정_프로젝트정보_200() {
        final var request = ProjectSteps.프로젝트수정요청_생성();
        final String token = userSteps.로그인토큰정보(UserSteps.로그인요청생성()).accessToken();
        final Long projectId = projectService.getProjectIdFromAccountId(UserSteps.accountId);

        given(this.spec)
            .filter(document(DEFAULT_RESTDOC_PATH, "프로젝트를 생성합니다", "프로젝트생성",
                UserDocument.AccessTokenHeader, ProjectDocument.ProjectIdPathParam,
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
