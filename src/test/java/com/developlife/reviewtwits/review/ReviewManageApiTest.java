package com.developlife.reviewtwits.review;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.CommonDocument;
import com.developlife.reviewtwits.CommonSteps;
import com.developlife.reviewtwits.entity.Product;
import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.message.response.project.ProjectInfoResponse;
import com.developlife.reviewtwits.project.ProjectSteps;
import com.developlife.reviewtwits.repository.ProductRepository;
import com.developlife.reviewtwits.repository.project.ProjectRepository;
import com.developlife.reviewtwits.service.ProjectService;
import com.developlife.reviewtwits.service.user.UserService;
import com.developlife.reviewtwits.user.UserDocument;
import com.developlife.reviewtwits.user.UserSteps;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static com.developlife.reviewtwits.review.ShoppingMallReviewSteps.*;
import static com.developlife.reviewtwits.review.ShoppingMallReviewSteps.리뷰_이미지_파일정보_생성;
import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author WhalesBob
 * @since 2023-05-19
 */
public class ReviewManageApiTest extends ApiTest {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserSteps userSteps;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProjectRepository projectRepository;
    private Product product;
    private Project project;
    private RegisterUserRequest registerUserRequest;
    private RegisterUserRequest registerOtherUserRequest;

    @BeforeEach
    void settings(){
        registerUserRequest = userSteps.회원가입정보_생성();
        userService.register(registerUserRequest, UserSteps.일반유저권한_생성());

        registerOtherUserRequest = userSteps.상대유저_회원가입정보_생성();
        userService.register(registerOtherUserRequest, UserSteps.일반유저권한_생성());

        User user = userService.getUser(UserSteps.accountId);

        ProjectInfoResponse projectInfoResponse = projectService.registerProject(ProjectSteps.프로젝트생성요청_생성(), user);
        project = projectRepository.findByProjectId(Long.parseLong(projectInfoResponse.projectId())).get();
        product = 임시_상품정보_생성(project, productRepository);
    }

    @Test
    void 리뷰_허가_APPROVED_성공_200(){

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long reviewId = 쇼핑몰_리뷰_등록(token);

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "입력한 리뷰 아이디의 리뷰를 허가 혹은 스팸 처리합니다." +
                                "<br>올바른 입력값을 입력했을 경우, 200 OK 와 함께 리뷰 처리 결과가 리턴됩니다." +
                                "<br>리뷰 아이디가 누락되었거나, APPROVED, SPAM 이외의 리뷰허가요청이 입력되었을 경우, 400 Bad Request 가 리턴됩니다." +
                                "<br>유저 토큰 정보가 헤더에 입력되지 않았을 때, 401 Unauthorized 가 리턴됩니다." +
                                "<br>해당 리뷰를 허가 처리할 권한이 없는 경우, 403 Forbidden 가 리턴됩니다" +
                                "<br>입력된 아이디로 등록된 리뷰가 없는 경우 404 Not Found 가 리턴됩니다." , "리뷰허가스팸관리",
                        UserDocument.AccessTokenHeader,
                        ReviewManageDocument.reviewApproveRequestField,
                        ReviewManageDocument.reviewApproveResponseField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(ReviewManageSteps.리뷰_허가요청_생성(reviewId))
                .when()
                .post("/review-management/approve")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getString("status")).isEqualTo("APPROVED");
    }

    @Test
    void 리뷰_허가_SPAM_성공_200(){

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        final String opposite = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        Long reviewId = 쇼핑몰_리뷰_등록(opposite);

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        UserDocument.AccessTokenHeader,
                        ReviewManageDocument.reviewApproveRequestField,
                        ReviewManageDocument.reviewApproveResponseField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(ReviewManageSteps.리뷰_허가_스팸요청_생성(reviewId))
                .when()
                .post("/review-management/approve")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getString("status")).isEqualTo("SPAM");
    }

    @Test
    void 리뷰_허가_잘못된허가내용_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long reviewId = 쇼핑몰_리뷰_등록(token);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(ReviewManageSteps.리뷰_허가요청_잘못된요청_생성(reviewId))
                .when()
                .post("/review-management/approve")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all().extract();
    }

    @Test
    void 리뷰_허가_리뷰아이디누락_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(ReviewManageSteps.리뷰_허가요청_리뷰아이디_누락())
                .when()
                .post("/review-management/approve")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all().extract();
    }

    @Test
    void 리뷰_허가_헤더정보없음_401(){

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long reviewId = 쇼핑몰_리뷰_등록(token);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(ReviewManageSteps.리뷰_허가요청_생성(reviewId))
                .when()
                .post("/review-management/approve")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all();
    }

    @Test
    void 리뷰_허가_권한없음_403(){

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        final String oppositeToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());
        Long reviewId = 쇼핑몰_리뷰_등록(token);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", oppositeToken)
                .body(ReviewManageSteps.리뷰_허가요청_생성(reviewId))
                .when()
                .post("/review-management/approve")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all();
    }

    @Test
    void 리뷰_허가_해당리뷰없음_404(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(ReviewManageSteps.리뷰_허가요청_잘못된아이디_생성())
                .when()
                .post("/review-management/approve")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all();
    }
    @Test
    void 리뷰_찾기_조건없이_통과_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        쇼핑몰_리뷰_등록(token);
        Long otherTokenReviewId = 쇼핑몰_리뷰_등록(otherToken);
        리뷰_허가(token, otherTokenReviewId);

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "리뷰 관리에서 리뷰를 검색하는 API 입니다." +
                        "<br>헤더의 X-AUTH-TOKEN, Query String 의 page,size 는 필수값입니다." +
                        "<br>X-AUTH-TOKEN 이 존재하지 않을 시 401 Unauthorized 가 발생합니다." +
                        "<br>size 가 존재하지 않거나, size 가 1 이상의 값이 아닐 시 400 Bad Request 가 발생합니다." +
                        "<br>Query String 에 status, sort, startDate, endDate, reviewId 는 선택적으로 입력할 수 있습니다." +
                        "<br>reviewId 는 1 이상의 값으로 입력해야 하며, 내림차순일 때는 가장 낮은 reviewId, 오름차순일 때는 가장 높은 reviewId 를 입력하면 그 다음 review 를 받을 수 있습니다." +
                        "<br>status 는 PENDING, APPROVED, SPAM, DELETED 중 하나를 입력할 수 있습니다." +
                        "<br>sort 는 NEWEST, OLDEST 중 하나를 입력할 수 있습니다." +
                        "<br>startDate, endDate 는 yyyy-MM-dd 형식의 날짜를 입력할 수 있습니다." +
                        "<br>startDate 와 endDate 는 현재 기준 이전으로 입력해야 합니다.","리뷰검색",
                        UserDocument.AccessTokenHeader,
                        ReviewManageDocument.reviewSearchRequestField,
                        ShoppingMallReviewDocument.shoppingMallReviewListResponseField))
                .header("X-AUTH-TOKEN", token)
                .param("size", 5)
                .when()
                .get("/review-management/search")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        long firstReceivedReviewId = jsonPath.getLong("[0].reviewId");
        long secondReceivedReviewId = jsonPath.getLong("[1].reviewId");

        assertThat(firstReceivedReviewId > secondReceivedReviewId).isTrue();
        assertThat(jsonPath.getString("[0].status")).isNotEqualTo(jsonPath.getString("[1].status"));
    }

    @Test
    void 리뷰_찾기_STATUS_설정_통과_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        쇼핑몰_리뷰_등록(token);
        Long otherTokenReviewId = 쇼핑몰_리뷰_등록(otherToken);
        리뷰_허가(token, otherTokenReviewId);

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        UserDocument.AccessTokenHeader,
                        ReviewManageDocument.reviewSearchRequestField,
                        ShoppingMallReviewDocument.shoppingMallReviewListResponseField))
                .header("X-AUTH-TOKEN", token)
                .param("size", 5)
                .param("status", "APPROVED")
                .when()
                .get("/review-management/search")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getString("[0].status")).isEqualTo("APPROVED");
        assertThat(jsonPath.getList("").size()).isEqualTo(1);
    }

    @Test
    void 리뷰_찾기_오름차순_설정_통과_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        쇼핑몰_리뷰_등록(token);
        Long otherTokenReviewId = 쇼핑몰_리뷰_등록(otherToken);
        리뷰_허가(token, otherTokenReviewId);

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        UserDocument.AccessTokenHeader,
                        ReviewManageDocument.reviewSearchRequestField,
                        ShoppingMallReviewDocument.shoppingMallReviewListResponseField))
                .header("X-AUTH-TOKEN", token)
                .param("size", 5)
                .param("sort","OLDEST")
                .when()
                .get("/review-management/search")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        long firstReceivedReviewId = jsonPath.getLong("[0].reviewId");
        long secondReceivedReviewId = jsonPath.getLong("[1].reviewId");

        assertThat(firstReceivedReviewId < secondReceivedReviewId).isTrue();
    }

    @Test
    void 리뷰_찾기_startDate_설정_통과_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        쇼핑몰_리뷰_등록(token);
        Long otherTokenReviewId = 쇼핑몰_리뷰_등록(otherToken);
        리뷰_허가(token, otherTokenReviewId);

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        UserDocument.AccessTokenHeader,
                        ReviewManageDocument.reviewSearchRequestField,
                        ShoppingMallReviewDocument.shoppingMallReviewListResponseField))
                .header("X-AUTH-TOKEN", token)
                .param("size", 5)
                .param("startDate", "2023-05-01")
                .when()
                .get("/review-management/search")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        List<Long> lastModifiedDate = jsonPath.getList("[0].lastModifiedDate", Long.class).subList(0, 4);
        assertThat(lastModifiedDate.get(0)).isGreaterThanOrEqualTo(2023);
        assertThat(lastModifiedDate.get(1)).isGreaterThanOrEqualTo(5);
        assertThat(lastModifiedDate.get(2)).isGreaterThanOrEqualTo(1);
    }

    @Test
    void 리뷰_찾기_endDate_설정_통과_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        쇼핑몰_리뷰_등록(token);
        Long otherTokenReviewId = 쇼핑몰_리뷰_등록(otherToken);
        리뷰_허가(token, otherTokenReviewId);

        String todayDate = LocalDate.now().toString();

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        UserDocument.AccessTokenHeader,
                        ReviewManageDocument.reviewSearchRequestField,
                        ShoppingMallReviewDocument.shoppingMallReviewListResponseField))
                .header("X-AUTH-TOKEN", token)
                .param("size", 5)
                .param("endDate", todayDate)
                .when()
                .get("/review-management/search")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        List<Long> lastModifiedDate = jsonPath.getList("[0].lastModifiedDate", Long.class).subList(0, 4);
        LocalDate now = LocalDate.now();
        assertThat(lastModifiedDate.get(0)).isLessThanOrEqualTo(now.getYear());
        assertThat(lastModifiedDate.get(1)).isLessThanOrEqualTo(now.getMonthValue());
        assertThat(lastModifiedDate.get(2)).isLessThanOrEqualTo(now.getDayOfMonth());
    }

    @Test
    void 리뷰_찾기_빈리스트_통과_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        쇼핑몰_리뷰_등록(token);
        Long otherTokenReviewId = 쇼핑몰_리뷰_등록(otherToken);
        리뷰_허가(token, otherTokenReviewId);

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        UserDocument.AccessTokenHeader,
                        ReviewManageDocument.reviewSearchRequestField))
                .header("X-AUTH-TOKEN", token)
                .param("size", 5)
                .param("status", "SPAM")
                .when()
                .get("/review-management/search")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getList("").size()).isEqualTo(0);
    }

    @Test
    void 리뷰_찾기_유저명의프로젝트없음_빈리스트_반환_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        쇼핑몰_리뷰_등록(token);
        Long otherTokenReviewId = 쇼핑몰_리뷰_등록(otherToken);
        리뷰_허가(token, otherTokenReviewId);

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        UserDocument.AccessTokenHeader,
                        ReviewManageDocument.reviewSearchRequestField))
                .header("X-AUTH-TOKEN", otherToken)
                .param("size", 5)
                .when()
                .get("/review-management/search")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getList("").size()).isEqualTo(0);
    }

    @Test
    void 리뷰_찾기_키워드_검색_성공_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        쇼핑몰_리뷰_등록(token);
        Long otherTokenReviewId = 쇼핑몰_리뷰_등록(otherToken);
        리뷰_허가(token, otherTokenReviewId);

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        UserDocument.AccessTokenHeader,
                        ReviewManageDocument.reviewSearchRequestField))
                .config(config().encoderConfig(encoderConfig()
                        .encodeContentTypeAs("x-www-form-urlencoded", ContentType.URLENC)
                        .defaultContentCharset("UTF-8")))
                .header("X-AUTH-TOKEN", token)
                .param("size", 5)
                .param("keyword", "맛있고+그랬어요")
                .when()
                .get("/review-management/search")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getList("").size()).isEqualTo(2);
    }

    @Test
    void 리뷰_찾기_페이지_설정_오름차순_성공_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        쇼핑몰_리뷰_등록(token);
        Long otherTokenReviewId = 쇼핑몰_리뷰_등록(otherToken);
        리뷰_허가(token, otherTokenReviewId);

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        UserDocument.AccessTokenHeader,
                        ReviewManageDocument.reviewSearchRequestField))
                .header("X-AUTH-TOKEN", token)
                .param("size", 1)
                .param("sort", "OLDEST")
                .when()
                .get("/review-management/search")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getList("").size()).isEqualTo(1);

        long lastReviewId = jsonPath.getLong("[0].reviewId");

        ExtractableResponse<Response> secondResponse = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        UserDocument.AccessTokenHeader,
                        ReviewManageDocument.reviewSearchRequestField))
                .header("X-AUTH-TOKEN", token)
                .param("size", 1)
                .param("sort", "OLDEST")
                .param("reviewId", lastReviewId)
                .when()
                .get("/review-management/search")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath secondJsonPath = secondResponse.jsonPath();
        assertThat(secondJsonPath.getLong("[0].reviewId") > lastReviewId).isTrue();
    }

    @Test
    void 리뷰_찾기_페이지_설정_내림차순_성공_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        쇼핑몰_리뷰_등록(token);
        Long otherTokenReviewId = 쇼핑몰_리뷰_등록(otherToken);
        리뷰_허가(token, otherTokenReviewId);

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        UserDocument.AccessTokenHeader,
                        ReviewManageDocument.reviewSearchRequestField))
                .header("X-AUTH-TOKEN", token)
                .param("size", 1)
                .when()
                .get("/review-management/search")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getList("").size()).isEqualTo(1);

        long lastReviewId = jsonPath.getLong("[0].reviewId");

        ExtractableResponse<Response> secondResponse = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        UserDocument.AccessTokenHeader,
                        ReviewManageDocument.reviewSearchRequestField))
                .header("X-AUTH-TOKEN", token)
                .param("size", 1)
                .param("reviewId", lastReviewId)
                .when()
                .get("/review-management/search")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath secondJsonPath = secondResponse.jsonPath();
        assertThat(secondJsonPath.getLong("[0].reviewId") < lastReviewId).isTrue();
    }

    @Test
    void 리뷰_찾기_reviewId_설정_이상_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        쇼핑몰_리뷰_등록(token);
        Long otherTokenReviewId = 쇼핑몰_리뷰_등록(otherToken);
        리뷰_허가(token, otherTokenReviewId);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("reviewId", -1)
                .param("size", 5)
                .when()
                .get("/review-management/search")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all().extract();
    }

    @Test
    void 리뷰_찾기_size_설정_이상_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        쇼핑몰_리뷰_등록(token);
        Long otherTokenReviewId = 쇼핑몰_리뷰_등록(otherToken);
        리뷰_허가(token, otherTokenReviewId);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .param("size", -1)
                .when()
                .get("/review-management/search")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all().extract();
    }

    @Test
    void 리뷰_찾기_유저헤더정보_누락_401(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        쇼핑몰_리뷰_등록(token);
        Long otherTokenReviewId = 쇼핑몰_리뷰_등록(otherToken);
        리뷰_허가(token, otherTokenReviewId);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .param("size", 5)
                .when()
                .get("/review-management/search")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all().extract();
    }

    private Long 쇼핑몰_리뷰_등록(String token){

        RequestSpecification request = given(this.spec).log().all()
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", token)
                .multiPart("productURL", productURL)
                .multiPart(CommonSteps.multipartText("content",rightReviewText))
                .multiPart("score", starScore);

        List<MultiPartSpecification> multiPartSpecList = 리뷰_이미지_파일정보_생성();

        for(MultiPartSpecification multiPartSpecification : multiPartSpecList){
            request.multiPart(multiPartSpecification);
        }

        ExtractableResponse<Response> response = request
                .when()
                .post("/reviews/shopping")
                .then().log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        return jsonPath.getLong("reviewId");
    }
    private void 리뷰_허가(String token, Long reviewId){
        given(this.spec)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(ReviewManageSteps.리뷰_허가요청_생성(reviewId))
                .when()
                .post("/review-management/approve")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();
    }
}