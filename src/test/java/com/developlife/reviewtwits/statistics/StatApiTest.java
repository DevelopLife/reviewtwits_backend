package com.developlife.reviewtwits.statistics;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.CommonDocument;
import com.developlife.reviewtwits.entity.Product;
import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.StatInfo;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.mapper.ProjectMapper;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.repository.ProductRepository;
import com.developlife.reviewtwits.repository.project.ProjectRepository;
import com.developlife.reviewtwits.repository.project.StatInfoRepository;
import com.developlife.reviewtwits.review.ShoppingMallReviewSteps;
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

import java.io.IOException;
import java.util.Optional;

import static com.developlife.reviewtwits.review.ShoppingMallReviewSteps.임시_상품정보_생성;
import static com.developlife.reviewtwits.review.ShoppingMallReviewSteps.임시_프로젝트정보_생성;
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
    private ProductRepository productRepository;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private ProjectRepository projectRepository;

    private RegisterUserRequest registerUserRequest;
    private User user;
    private Product product;
    private Project project;


    @BeforeEach
    void settings() throws IOException {
        registerUserRequest = userSteps.회원가입정보_생성();
        user = userService.register(registerUserRequest, UserSteps.일반유저권한_생성());
        final String userToken = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        추가회원가입정보_입력(userToken, SnsSteps.userNickname);

        project = 임시_프로젝트정보_생성(projectMapper, projectRepository);
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
                                "<br>입력한 productUrl 로 등록된 제품 정보가 존재하지 않을 경우, 404 Not Found 가 반환됩니다.", "통계정보등록요청",
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