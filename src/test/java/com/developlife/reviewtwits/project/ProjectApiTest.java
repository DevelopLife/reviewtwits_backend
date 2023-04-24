package com.developlife.reviewtwits.project;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.CommonDocument;
import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.StatInfo;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.repository.project.ProjectRepository;
import com.developlife.reviewtwits.repository.project.StatInfoRepository;
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
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    @Autowired
    StatInfoRepository statInfoRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

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
                    i -> projectService.registerProject(ProjectSteps.프로젝트생성요청_생성(), user));
    }


    @Test
    @DisplayName("프로젝트 생성")
    public void 프로젝트생성_프로젝트정보_200() {
        final var request = ProjectSteps.프로젝트생성요청_생성();
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "프로젝트를 생성합니다", "프로젝트생성",
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
    @DisplayName("프로젝트 리스트")
    public void 프로젝트리스트_프로젝트정보_200() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
            .filter(document(DEFAULT_RESTDOC_PATH, "유저의 프로젝트를 리스트를 반환합니다", "프로젝트리스트", CommonDocument.AccessTokenHeader, ProjectDocument.ProjectInfoListResponseField))
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

    @Test
    void 일간_방문_통계정보_검색성공_200() {
        Project project = 통계_사전작업();
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "일간 방문 통계정보를 검색하면, 올바른 입력값일 경우 200 OK 와 함께 정보가 반환됩니다.." +
                                "<br>해당 유저가 가지고 있는 프로젝트의 아이디를 입력해야 합니다." +
                                "<br>그리고 일간 방문 통계정보의 범위를 입력해야 합니다. " +
                                "<br>범위로써 입력할 수 있는 정보는 아래와 같습니다." +
                                "<br><br> 1d,3d,5d,7d,15d" +
                                "<br> 1mo,3mo,6mo,1y,3y,5y" +
                                "<br><br>위의 규칙에 맞지 않는 입력값일 경우, 400 Bad Request 가 반환됩니다." +
                                "<br>헤더에 토큰 정보가 누락되었을 경우, 401 Unauthorized 가 반환됩니다." +
                                "<br>해당 유저가 프로젝트를 소유하지 않을 경우, 403 Forbidden 이 반환됩니다.", "일간방문통계정보검색",
                        CommonDocument.AccessTokenHeader,
                        ProjectDocument.DailyVisitStatRequestParam,
                        ProjectDocument.DailyVisitInfoResponseFields
                        ))
                .header("X-AUTH-TOKEN", token)
                .param("projectId", project.getProjectId())
                .param("range", ProjectSteps.exampleRange)
                .when()
                .get("/projects/daily-visit-infos")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getString("range")).isEqualTo(ProjectSteps.exampleRange);
        assertThat(jsonPath.getList("visitInfo.timeStamp")).size().isEqualTo(12);
        assertThat(jsonPath.getList("visitInfo.visitCount")).size().isEqualTo(12);
        assertThat(jsonPath.getList("visitInfo.previousCompare")).size().isEqualTo(12);
    }

    @Test
    void 일간_방문_통계정보_헤더정보없음_401() {
        Project project = 통계_사전작업();

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .param("projectId", project.getProjectId())
                .param("range", ProjectSteps.exampleRange)
                .when()
                .get("/projects/daily-visit-infos")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all();
    }

    @Test
    void 일간_방문_통계정보_접근권한_없음_403() {
        Project project = 통계_사전작업();
        final String token = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectId", project.getProjectId())
                .param("range", ProjectSteps.exampleRange)
                .when()
                .get("/projects/daily-visit-infos")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all();
    }

    @Test
    void 일간_방문_통계정보_프로젝트아이디_없음_404() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectId", ProjectSteps.notExistProjectId)
                .param("range", ProjectSteps.exampleRange)
                .when()
                .get("/projects/daily-visit-infos")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all().extract();
    }

    @Test
    void 일간_방문_통계정보_프로젝트아이디_음수_400() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectId", ProjectSteps.wrongProjectId)
                .param("range", ProjectSteps.exampleRange)
                .when()
                .get("/projects/daily-visit-infos")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all().extract();
    }
    @Test
    void 일간_방문_통계정보_검색시간구간_허용외값_400() {
        Project project = 통계_사전작업();
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectId", project.getProjectId())
                .param("range", ProjectSteps.wrongRange)
                .when()
                .get("/projects/daily-visit-infos")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all().extract();
    }

    @Test
    void 최근방문_통계정보_요청_성공_200(){
        Project project = 통계_사전작업();
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        CommonDocument.AccessTokenHeader,
                        ProjectDocument.ProjectIdRequestParam,
                        ProjectDocument.RecentVisitStatResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectId", project.getProjectId())
                .when()
                .get("/projects/recent-visit-counts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getInt("todayVisit")).isEqualTo(3);
        assertThat(jsonPath.getInt("yesterdayVisit")).isEqualTo(2);
        assertThat(jsonPath.getInt("totalVisit")).isEqualTo(20);
    }

    @Test
    void 최근방문_통계정보_요청_헤더정보없음_401(){
        Project project = 통계_사전작업();

        given(this.spec)
                .param("projectId", project.getProjectId())
                .when()
                .get("/projects/recent-visit-counts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all();
    }

    @Test
    void 최근방문_통계정보_요청_접근권한없음_403(){
        Project project = 통계_사전작업();
        final String token = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        given(this.spec)
                .header("X-AUTH-TOKEN", token)
                .param("projectId", project.getProjectId())
                .when()
                .get("/projects/recent-visit-counts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all();
    }

    @Test
    void 최근방문_통계정보_요청_프로젝트아이디_존재하지않음_404(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .header("X-AUTH-TOKEN", token)
                .param("projectId", ProjectSteps.notExistProjectId)
                .when()
                .get("/projects/recent-visit-counts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all();
    }

    @Test
    void 최근방문_통계정보_요청_프로젝트아이디음수_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .header("X-AUTH-TOKEN", token)
                .param("projectId", ProjectSteps.wrongProjectId)
                .when()
                .get("/projects/recent-visit-counts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all();
    }


    Project 통계_사전작업() {
        Project existedProject = projectRepository.findAll().get(0);

        ArrayList<StatInfo> statInfos = new ArrayList<>();
        for (int day = 1; day <= 10; day++) {
            statInfos.add(ProjectSteps.통계정보_생성(existedProject, 2023, 3, day, 1));
        }
        for (int day = 1; day <= 5; day++) {
            statInfos.add(ProjectSteps.통계정보_생성(existedProject, 2023, 3, day, 2));
        }

        LocalDateTime now = LocalDateTime.now();
        for (int hour = 1; hour <= 3; hour++) {
            statInfos.add(ProjectSteps.통계정보_생성(existedProject, now.getYear(),
                    now.getMonthValue(),
                    now.getDayOfMonth(),
                    hour));
        }

        for (int hour = 1; hour <= 2; hour++) {
            statInfos.add(ProjectSteps.통계정보_생성(existedProject, now.getYear(),
                    now.getMonthValue(),
                    now.getDayOfMonth() - 1,
                    hour));
        }
        saveAll(statInfos);
        return existedProject;
    }

    void saveAll(List<StatInfo> statInfos) {
        int index = 1;
        for (StatInfo statInfo : statInfos) {
            jdbcTemplate.update("insert into stat_info (stat_id,project_project_id,created_date) values (?,?,?)",
                    index,
                    statInfo.getProject().getProjectId(),
                    Timestamp.valueOf(statInfo.getCreatedDate()));

            index++;
        }
    }
}
