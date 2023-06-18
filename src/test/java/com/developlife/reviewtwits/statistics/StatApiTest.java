package com.developlife.reviewtwits.statistics;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.CommonDocument;
import com.developlife.reviewtwits.entity.Product;
import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.StatInfo;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.project.ProjectDocument;
import com.developlife.reviewtwits.project.ProjectSteps;
import com.developlife.reviewtwits.repository.ProductRepository;
import com.developlife.reviewtwits.repository.ProjectRepository;
import com.developlife.reviewtwits.repository.statistics.StatInfoRepository;
import com.developlife.reviewtwits.review.ShoppingMallReviewSteps;
import com.developlife.reviewtwits.service.ProjectService;
import com.developlife.reviewtwits.service.user.UserService;
import com.developlife.reviewtwits.sns.SnsSteps;
import com.developlife.reviewtwits.user.UserSteps;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.developlife.reviewtwits.review.ShoppingMallReviewSteps.임시_상품정보_생성;
import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author WhalesBob
 * @since 2023-04-23
 */
public class StatApiTest extends ApiTest {

    @Autowired
    private StatInfoRepository statInfoRepository;
    @Autowired
    private UserSteps userSteps;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RegisterUserRequest registerUserRequest;
    private RegisterUserRequest registerOtherUserRequest;

    private User user;
    private User otherUser;
    private Product product;
    private Project project;


    @BeforeEach
    void settings() throws IOException {
        registerUserRequest = userSteps.회원가입정보_생성();
        registerOtherUserRequest = userSteps.상대유저_회원가입정보_생성();

        user = userService.register(registerUserRequest, UserSteps.일반유저권한_생성());
        otherUser = userService.register(registerOtherUserRequest, UserSteps.일반유저권한_생성());

        final String userToken = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        추가회원가입정보_입력(userToken, SnsSteps.userNickname);

        projectService.registerProject(ProjectSteps.프로젝트생성요청_생성(0), user);
        project = projectRepository.findAll().get(0);

        product = 임시_상품정보_생성(project, productRepository);
    }


    @Test
    void 통계정보_등록_성공_200(){

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "통계정보 등록 요청을 보냅니다." +
                                "<br>모든 정보가 정상적으로 입력되었다면, 200 OK 와 함께 등록된 통계정보가 반환됩니다." +
                                "<br>inflowUrl, productUrl 정보가 입력되지 않았거나 http 또는 https 로 시작하는 url 이 아닐 시, 400 Bad Request 가 반환됩니다." +
                                "<br>MOBILE,PC 를 제외한 다른 Device 정보를 입력할 경우, 400 Bad Request 가 반환됩니다. 디바이스 관련 정보는 요청 시 추가 가능합니다." +
                                "<br>입력한 productUrl 로 등록된 제품 정보가 존재하지 않을 경우, 202 Accepted 가 반환됩니다.", "통계정보등록요청",
                        StatDocument.AccessTokenHeader,
                        StatDocument.statMessageRequestField,
                        StatDocument.savedStatResponseField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(StatInfoSteps.통계정보_생성())
                .when()
                .post("/statistics/visited-info")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        long statId = jsonPath.getLong("statId");
        Optional<StatInfo> foundStatInfo = statInfoRepository.findById(statId);

        assertThat(foundStatInfo).isPresent();
        assertThat(jsonPath.getString("createdDate")).isNotEmpty();
        assertThat(jsonPath.getString("productUrl")).isEqualTo(StatInfoSteps.productUrl);
        assertThat(jsonPath.getString("deviceInfo")).isEqualTo(StatInfoSteps.device);
        assertThat(jsonPath.getString("inflowUrl")).isEqualTo(StatInfoSteps.inflowUrl);
    }

    @Test
    void 통계정보_등록_유저정보없음_성공_200(){

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        StatDocument.AccessTokenHeader,
                        StatDocument.statMessageRequestField,
                        StatDocument.savedStatResponseField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(StatInfoSteps.통계정보_생성())
                .when()
                .post("/statistics/visited-info")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();
    }

    @Test
    void 통계정보_등록_유입정보없음_성공_200(){

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        StatDocument.AccessTokenHeader,
                        StatDocument.statMessageRequestField,
                        StatDocument.savedStatResponseField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(StatInfoSteps.통계정보_생성_inflow_미포함())
                .when()
                .post("/statistics/visited-info")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();
    }
    @Test
    void 통계정보_등록_URL_형식아님_400(){
        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(StatInfoSteps.통계정보_생성_URL_형식아님())
                .when()
                .post("/statistics/visited-info")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all().extract();
    }

    @Test
    void 통계정보_등록_디바이스정보_예외_400(){
        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(StatInfoSteps.통계정보_생성_device_형식아님())
                .when()
                .post("/statistics/visited-info")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all().extract();
    }

    @Test
    void 통계정보_등록_URL_미포함_400(){
        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(StatInfoSteps.통계정보_생성_productUrl_미포함())
                .when()
                .post("/statistics/visited-info")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all().extract();
    }

    @Test
    void 통계정보_등록_디바이스정보_미포함_400(){
        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(StatInfoSteps.통계정보_생성_device_미포함())
                .when()
                .post("/statistics/visited-info")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all().extract();
    }

    @Test
    void 통계정보_등록_등록되지않은_상품_202(){
        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(StatInfoSteps.통계정보_생성_등록되지않은_상품_URL())
                .when()
                .post("/statistics/visited-info")
                .then()
                .assertThat()
                .statusCode(HttpStatus.ACCEPTED.value())
                .log().all().extract();
    }

    @Test
    void 일간_방문_통계정보_검색성공_200() {
        Project project = 통계_사전작업();
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "일간 방문 통계정보를 검색하면, 올바른 입력값일 경우 200 OK 와 함께 정보가 반환됩니다.." +
                                "<br>해당 유저가 가지고 있는 프로젝트의 이름을 입력해야 합니다. 영어, 숫자, '-', '_' 만 포함되어 있는 문자열만 입력해야 합니다." +
                                "<br>그리고 일간 방문 통계정보의 범위를 입력해야 합니다. " +
                                "<br>범위로써 입력할 수 있는 정보는 아래와 같습니다." +
                                "<br><br> 1d,3d,5d,7d,15d" +
                                "<br> 1mo,3mo,6mo,1y,3y,5y" +
                                "<br><br>위의 규칙에 맞지 않는 입력값일 경우, 400 Bad Request 가 반환됩니다." +
                                "<br>헤더에 토큰 정보가 누락되었을 경우, 401 Unauthorized 가 반환됩니다." +
                                "<br>해당 유저가 프로젝트를 소유하지 않을 경우, 403 Forbidden 이 반환됩니다." +
                                "<br>입력받은 프로젝트 아이디로 된 프로젝트를 찾을 수 없을 경우, 404 Not Found 가 반환됩니다.", "일간방문통계그래프정보",
                        CommonDocument.AccessTokenHeader,
                        StatDocument.DailyVisitStatRequestParam,
                        StatDocument.DailyVisitInfoResponseFields
                ))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", project.getProjectName())
                .param("range", ProjectSteps.exampleRange)
                .when()
                .get("/statistics/daily-visit-graph-infos")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getString("range")).isEqualTo(ProjectSteps.exampleRange);
    }

    @Test
    void 일간_방문_통계정보_헤더정보없음_401() {
        Project project = 통계_사전작업();

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .param("projectName", project.getProjectName())
                .param("range", ProjectSteps.exampleRange)
                .when()
                .get("/statistics/daily-visit-graph-infos")
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
                .param("projectName", project.getProjectName())
                .param("range", ProjectSteps.exampleRange)
                .when()
                .get("/statistics/daily-visit-graph-infos")
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
                .param("projectName", ProjectSteps.notExistProjectName)
                .param("range", ProjectSteps.exampleRange)
                .when()
                .get("/statistics/daily-visit-graph-infos")
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
                .param("projectName", ProjectSteps.wrongProjectName)
                .param("range", ProjectSteps.exampleRange)
                .when()
                .get("/statistics/daily-visit-graph-infos")
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
                .param("projectName", project.getProjectName())
                .param("range", ProjectSteps.wrongRange)
                .when()
                .get("/statistics/daily-visit-graph-infos")
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
                .filter(document(DEFAULT_RESTDOC_PATH, "최근 방문 통계정보를 검색하면, 올바른 입력값일 경우 200 OK 와 함께 정보가 반환됩니다.." +
                                "<br>해당 유저가 가지고 있는 프로젝트의 이름을 입력해야 합니다. 영어, 숫자, '-', '_' 만 포함되어 있는 문자열만 입력해야 합니다." +
                                "<br>헤더에 토큰 정보가 누락되었을 경우, 401 Unauthorized 가 반환됩니다." +
                                "<br>해당 유저가 프로젝트를 소유하지 않을 경우, 403 Forbidden 이 반환됩니다." +
                                "<br>입력받은 프로젝트 아이디로 된 프로젝트를 찾을 수 없을 경우, 404 Not Found 가 반환됩니다.",
                        "최근방문통계정보요청",
                        CommonDocument.AccessTokenHeader,
                        ProjectDocument.ProjectNameRequestParam,
                        StatDocument.RecentVisitStatResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", project.getProjectName())
                .when()
                .get("/statistics/recent-visit-counts")
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
                .filter(document(DEFAULT_RESTDOC_PATH))
                .param("projectName", project.getProjectName())
                .when()
                .get("/statistics/recent-visit-counts")
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
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", project.getProjectName())
                .when()
                .get("/statistics/recent-visit-counts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all();
    }

    @Test
    void 최근방문_통계정보_요청_프로젝트아이디_존재하지않음_404(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", ProjectSteps.notExistProjectName)
                .when()
                .get("/statistics/recent-visit-counts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all();
    }

    @Test
    void 최근방문_통계정보_요청_프로젝트이름_형식오류_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", ProjectSteps.wrongProjectName)
                .when()
                .get("/statistics/recent-visit-counts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all();
    }
    @Test
    void 방문수_그래프_정보_요청_성공_200(){
        Project project = 통계_사전작업();
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "방문수 그래프 정보를 요청하면, 올바른 입력값일 경우 200 OK 와 함께 통걔 정보가 반환됩니다." +
                                "<br>헤더에 토큰 정보가 누락되었을 경우, 401 Unauthorized 가 반환됩니다." +
                                "<br>해당 유저가 프로젝트를 소유하지 않을 경우, 403 Forbidden 이 반환됩니다." +
                                "<br>해당 유저가 가지고 있는 프로젝트의 이름을 입력해야 합니다. 영어, 숫자, '-', '_' 만 포함되어 있는 문자열만 입력해야 합니다." +
                                "<br>입력받은 프로젝트 이름으로 된 프로젝트를 찾을 수 없을 경우, 404 Not Found 가 반환됩니다." +
                                "<br>요청 마지막 날짜(endDate)는 yyyy-mm-dd 형식으로 입력해야 하며, 선택적으로 입력할 수 있습니다." +
                                "<br>입력하지 않았을 시 현재 날짜 기준으로 그래프 정보가 반환되며, 현재 날짜 이후의 날짜를 입력할 수 없습니다." +
                                "<br>또한 통계 범위와 구간을 입력해야 하며, 범위로써 입력할 수 있는 정보는 아래와 같습니다." +
                                "<br><br> 1d,3d,5d,7d,15d" +
                                "<br> 1mo,3mo,6mo,1y,3y,5y" +
                                "<br><br>위의 규칙에 맞지 않는 입력값일 경우, 400 Bad Request 가 반환됩니다.",
                        "방문수그래프정보요청",
                        CommonDocument.AccessTokenHeader,
                        StatDocument.VisitGraphInfoRequestParamFields,
                        StatDocument.VisitGraphStatResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", project.getProjectName())
                .param("interval", ProjectSteps.exampleInterval)
                .param("range", ProjectSteps.exampleRange)
                .param("endDate", ProjectSteps.exampleEndDate)
                .when()
                .get("/statistics/visit-graph-infos")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getString("interval")).isEqualTo(ProjectSteps.exampleInterval);
        assertThat(jsonPath.getString("range")).isEqualTo(ProjectSteps.exampleRange);
        assertThat(jsonPath.getInt("todayVisit")).isEqualTo(3);
        assertThat(jsonPath.getInt("yesterdayVisit")).isEqualTo(2);
        assertThat(jsonPath.getList("visitInfo.timeStamp")).isNotEmpty();
        assertThat(jsonPath.getList("visitInfo.visitCount")).isNotEmpty();
        assertThat(jsonPath.getList("visitInfo.previousCompare")).isNotEmpty();

        String lastTimeStamp = jsonPath.getList("visitInfo.timeStamp").get(jsonPath.getList("visitInfo.timeStamp").size()-1).toString();
        LocalDate lastDate = LocalDate.parse(lastTimeStamp);
        assertThat(lastDate.isBefore(LocalDate.parse(ProjectSteps.exampleEndDate))).isTrue();
    }

    @Test
    void 방문수_그래프_정보_요청_성공_endDate_없음_200(){
        Project project = 통계_사전작업();
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        ExtractableResponse<Response> response = given(this.spec)
                .header("X-AUTH-TOKEN", token)
                .param("projectName", project.getProjectName())
                .param("interval", ProjectSteps.exampleInterval)
                .param("range", ProjectSteps.exampleRange)
                .when()
                .get("/statistics/visit-graph-infos")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getString("interval")).isEqualTo(ProjectSteps.exampleInterval);
        assertThat(jsonPath.getString("range")).isEqualTo(ProjectSteps.exampleRange);
        assertThat(jsonPath.getInt("todayVisit")).isEqualTo(3);
        assertThat(jsonPath.getInt("yesterdayVisit")).isEqualTo(2);
        assertThat(jsonPath.getList("visitInfo.timeStamp")).isNotEmpty();
        assertThat(jsonPath.getList("visitInfo.visitCount")).isNotEmpty();
        assertThat(jsonPath.getList("visitInfo.previousCompare")).isNotEmpty();
    }

    @Test
    void 방문수_그래프_정보_요청_헤더정보없음_401(){
        Project project = 통계_사전작업();

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .param("projectName", project.getProjectName())
                .param("interval", ProjectSteps.exampleInterval)
                .param("range", ProjectSteps.exampleRange)
                .when()
                .get("/statistics/visit-graph-infos")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all();
    }

    @Test
    void 방문수_그래프_정보_요청_접근권한없음_403(){
        Project project = 통계_사전작업();
        final String token = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", project.getProjectName())
                .param("interval", ProjectSteps.exampleInterval)
                .param("range", ProjectSteps.exampleRange)
                .when()
                .get("/statistics/visit-graph-infos")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all();
    }

    @Test
    void 방문수_그래프_정보_요청_프로젝트없음_404(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", ProjectSteps.notExistProjectName)
                .param("interval", ProjectSteps.exampleInterval)
                .param("range", ProjectSteps.exampleRange)
                .when()
                .get("/statistics/visit-graph-infos")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all();
    }

    @Test
    void 방문수_그래프_정보_요청_프로젝트이름_형식오류_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", ProjectSteps.wrongProjectName)
                .param("interval", ProjectSteps.exampleInterval)
                .param("range", ProjectSteps.exampleRange)
                .when()
                .get("/statistics/visit-graph-infos")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all();
    }

    @Test
    void 방문수_그래프_정보_요청_검색시간구간_허용외값_400(){
        Project project = 통계_사전작업();
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", project.getProjectName())
                .param("interval", ProjectSteps.exampleInterval)
                .param("range", ProjectSteps.wrongRange)
                .when()
                .get("/statistics/visit-graph-infos")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all();
    }

    @Test
    void 대시보드_상품정보_통계_요청_성공_200(){
        Project project = 통계_사전작업();
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "상품정보 통계 자료를 요청하는 API 입니다." +
                        "<br>프로젝트 이름을 통해 해당 프로젝트의 상품정보 통계 자료를 요청합니다." +
                        "<br>해당 유저가 가지고 있는 프로젝트의 이름을 입력해야 합니다. 영어, 숫자, '-', '_' 만 포함되어 있는 문자열만 입력해야 합니다." +
                        "<br>프로젝트 이름을 입력하지 않거나, 잘못된 형식의 값을 입력하면 400 Bad Request 가 반환됩니다." +
                        "<br>헤더에 유저 정보를 입력하지 않으면 401 Unauthorized 가 반환됩니다." +
                        "<br>해당 프로젝트 아이디로 된 프로젝트에, 유저가 접근할 권한이 없으면 403 Forbidden 이 반환됩니다." +
                        "<br>해당 프로젝트 아이디로 된 프로젝트가 존재하지 않으면 404 Not Found 가 반환됩니다.", "대시보드 상품정보 통계 요청",
                        CommonDocument.AccessTokenHeader,
                        StatDocument.projectNameRequestParamField,
                        StatDocument.productStatisticsResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", project.getProjectName())
                .when()
                .get("/statistics/dashboard/product-statistics")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();
    }

    @Test
    void 대시보드_상품정보_통계_헤더정보없음_401(){
        Project project = 통계_사전작업();

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .param("projectName", project.getProjectName())
                .when()
                .get("/statistics/dashboard/product-statistics")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all().extract();
    }

    @Test
    void 대시보드_상품정보_통계_접근권한없음_403(){
        Project project = 통계_사전작업();
        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", otherToken)
                .param("projectName", project.getProjectName())
                .when()
                .get("/statistics/dashboard/product-statistics")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all().extract();
    }

    @Test
    void 대시보드_상품정보_통계_프로젝트이름_등록안됨_404(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", ProjectSteps.notExistProjectName)
                .when()
                .get("/statistics/dashboard/product-statistics")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all().extract();
    }

    @Test
    void 대시보드_상품정보_통계_프로젝트이름_형식오류_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", ProjectSteps.wrongProjectName)
                .when()
                .get("/statistics/dashboard/product-statistics")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all().extract();
    }

    @Test
    void 유입경로_통계_요청_성공_200(){
        Project project = 통계_사전작업();
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "어떤 유입경로로 사람들이 들어오는지 정보를 주는 API 입니다." +
                        "<br>프로젝트 이름을 통해 해당 프로젝트의 유입경로 통계 자료를 요청합니다." +
                        "<br>해당 유저가 가지고 있는 프로젝트의 이름을 입력해야 합니다. 영어, 숫자, '-', '_' 만 포함되어 있는 문자열만 입력해야 합니다." +
                        "<br>헤더에 유저 정보가 존재하지 않으면 401 Unauthorized 가 반환됩니다." +
                        "<br>해당 프로젝트 아이디로 된 프로젝트에, 유저가 접근할 권한이 없으면 403 Forbidden 이 반환됩니다." +
                        "<br>해당 프로젝트 아이디로 된 프로젝트가 존재하지 않으면 404 Not Found 가 반환됩니다.", "유입경로 통계 요청",
                        CommonDocument.AccessTokenHeader,
                        StatDocument.projectNameRequestParamField,
                        StatDocument.requestInflowInfosResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", project.getProjectName())
                .when()
                .get("/statistics/request-inflow-infos")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();
    }

    @Test
    void 유입경로_통계_헤더정보없음_401(){
        Project project = 통계_사전작업();

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .param("projectName", project.getProjectName())
                .when()
                .get("/statistics/request-inflow-infos")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all().extract();
    }

    @Test
    void 유입경로_통계_접근권한없음_403(){
        Project project = 통계_사전작업();
        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", otherToken)
                .param("projectName", project.getProjectName())
                .when()
                .get("/statistics/request-inflow-infos")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all().extract();
    }

    @Test
    void 유입경로_통계_프로젝트이름_등록안됨_404(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", ProjectSteps.notExistProjectName)
                .when()
                .get("/statistics/request-inflow-infos")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all().extract();
    }

    @Test
    void 유입경로_통계_프로젝트이름_형식오류_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", ProjectSteps.wrongProjectName)
                .when()
                .get("/statistics/request-inflow-infos")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all().extract();
    }
    @Test
    void 프로젝트_요약_통계_요청_성공_200(){
        Project project = 통계_사전작업();
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "프로젝트의 요약 통계 정보를 요청하는 API 입니다." +
                        "<br>프로젝트 이름을 통해 해당 프로젝트의 요약 통계 정보를 요청합니다." +
                        "<br>해당 유저가 가지고 있는 프로젝트의 이름을 입력해야 합니다. 영어, 숫자, '-', '_' 만 포함되어 있는 문자열만 입력해야 합니다." +
                        "<br>헤더에 유저 정보가 존재하지 않으면 401 Unauthorized 가 반환됩니다." +
                        "<br>해당 프로젝트 아이디로 된 프로젝트에, 유저가 접근할 권한이 없으면 403 Forbidden 이 반환됩니다." +
                        "<br>해당 프로젝트 아이디로 된 프로젝트가 존재하지 않으면 404 Not Found 가 반환됩니다.", "프로젝트 요약 통계 요청",
                        CommonDocument.AccessTokenHeader,
                        StatDocument.projectNameRequestParamField,
                        StatDocument.simpleProjectInfoResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", project.getProjectName())
                .when()
                .get("/statistics/dashboard/simple-project-info")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();
    }

    @Test
    void 프로젝트_요약_통계_헤더정보없음_401(){
        Project project = 통계_사전작업();
        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .param("projectName", project.getProjectName())
                .when()
                .get("/statistics/dashboard/simple-project-info")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all().extract();
    }

    @Test
    void 프로젝트_요약_통계_접근권한없음_403(){
        Project project = 통계_사전작업();
        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", otherToken)
                .param("projectName", project.getProjectName())
                .when()
                .get("/statistics/dashboard/simple-project-info")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all().extract();
    }

    @Test
    void 프로젝트_요약_통계_프로젝트이름_등록안됨_404(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", ProjectSteps.notExistProjectName)
                .when()
                .get("/statistics/dashboard/simple-project-info")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all().extract();
    }

    @Test
    void 프로젝트_요약_통계_프로젝트이름_형식오류_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", ProjectSteps.wrongProjectName)
                .when()
                .get("/statistics/dashboard/simple-project-info")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all().extract();
    }
    @Test
    void 리드타임_통계_요청_성공_200(){
        Project project = 통계_사전작업();
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "프로젝트의 리드타임 통계 정보를 요청하는 API 입니다." +
                        "<br>프로젝트 이름을 통해 해당 프로젝트의 리드타임 통계 정보를 요청합니다." +
                        "<br>해당 유저가 가지고 있는 프로젝트의 이름을 입력해야 합니다. 영어, 숫자, '-', '_' 만 포함되어 있는 문자열만 입력해야 합니다." +
                        "<br>헤더에 유저 정보가 존재하지 않으면 401 Unauthorized 가 반환됩니다." +
                        "<br>해당 프로젝트 아이디로 된 프로젝트에, 유저가 접근할 권한이 없으면 403 Forbidden 이 반환됩니다." +
                        "<br>해당 프로젝트 아이디로 된 프로젝트가 존재하지 않으면 404 Not Found 가 반환됩니다.", "리드타임 통계 요청",
                        CommonDocument.AccessTokenHeader,
                        StatDocument.projectNameRequestParamField))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", project.getProjectName())
                .when()
                .get("/statistics/readtime-info")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();
    }

    @Test
    void 리드타임_통계_요청_헤더정보없음_401(){
        Project project = 통계_사전작업();

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .param("projectName", project.getProjectName())
                .when()
                .get("/statistics/readtime-info")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all().extract();
    }

    @Test
    void 리드타임_통계_요청_접근권한없음_403(){
        Project project = 통계_사전작업();
        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", otherToken)
                .param("projectName", project.getProjectName())
                .when()
                .get("/statistics/readtime-info")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all().extract();
    }

    @Test
    void 리드타임_통계_요청_프로젝트이름_등록안됨_404(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", ProjectSteps.notExistProjectName)
                .when()
                .get("/statistics/readtime-info")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all().extract();
    }

    @Test
    void 리드타임_통계_요청_프로젝트이름_형식오류_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("projectName", ProjectSteps.wrongProjectName)
                .when()
                .get("/statistics/readtime-info")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all().extract();
    }

    Project 통계_사전작업() {
        Project existedProject = projectRepository.findAll().get(0);

        ArrayList<StatInfo> statInfos = new ArrayList<>();
        for (int day = 1; day <= 10; day++) {
            statInfos.add(ProjectSteps.통계정보_생성(existedProject, product,user,2023, 3, day, 1));
        }
        for (int day = 1; day <= 5; day++) {
            statInfos.add(ProjectSteps.통계정보_생성(existedProject, product,user,2023, 3, day, 2));
        }

        LocalDateTime now = LocalDateTime.now();
        int testYear = now.getYear();
        int testMonth = now.getMonthValue();
        int testDay = now.getDayOfMonth();
        for (int hour = 1; hour <= 3; hour++) {
            statInfos.add(ProjectSteps.통계정보_생성(existedProject, product,user,testYear, testMonth, testDay, hour));
        }

        int yesterday = LocalDateTime.now().minusDays(1).getDayOfMonth();
        int yesterdayMonth = LocalDateTime.now().minusDays(1).getMonthValue();
        for (int hour = 1; hour <= 2; hour++) {
            statInfos.add(ProjectSteps.통계정보_생성(existedProject, product, user, testYear, yesterdayMonth, yesterday, hour));
        }
        saveAll(statInfos);
        return existedProject;
    }

    void saveAll(List<StatInfo> statInfos) {
        int index = 1;
        for (StatInfo statInfo : statInfos) {
            jdbcTemplate.update("insert into stat_info (stat_id,project_project_id,created_date,product_product_id,user_user_id) values (?,?,?,?,?)",
                    index,
                    statInfo.getProject().getProjectId(),
                    Timestamp.valueOf(statInfo.getCreatedDate()),
                    statInfo.getProduct().getProductId(),
                    statInfo.getUser().getUserId());
            index++;
        }
    }

    void 추가회원가입정보_입력(String token, String nickname) throws IOException {
        MultiPartSpecification profileImage = ShoppingMallReviewSteps.프로필_이미지_파일정보생성();

        given(this.spec)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", token)
                .multiPart("nickname", nickname)
                .multiPart("introduceText", "test")
                .multiPart(profileImage)
                .when()
                .post("/users/register-addition")
                .then()
                .log().all();
    }
}