package com.developlife.reviewtwits.review;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.CommonDocument;
import com.developlife.reviewtwits.CommonSteps;
import com.developlife.reviewtwits.entity.Product;
import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.mapper.ProjectMapper;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.repository.ProductRepository;
import com.developlife.reviewtwits.repository.ProjectRepository;
import com.developlife.reviewtwits.repository.file.FileManagerRepository;
import com.developlife.reviewtwits.repository.review.ReviewRepository;
import com.developlife.reviewtwits.service.user.UserService;
import com.developlife.reviewtwits.type.ReferenceType;
import com.developlife.reviewtwits.user.UserDocument;
import com.developlife.reviewtwits.user.UserSteps;
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

import java.io.IOException;
import java.util.List;

import static com.developlife.reviewtwits.review.ShoppingMallReviewSteps.*;
import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;


/**
 * @author WhalesBob
 * @since 2023-03-13
 */
public class ShoppingMallReviewApiTest extends ApiTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserSteps userSteps;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private FileManagerRepository fileManagerRepository;
    @Autowired
    private ReviewRepository reviewRepository;

//    @Autowired
//    private AmazonS3 s3Client;

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

        project = 임시_프로젝트정보_생성(projectMapper, projectRepository);
        product = 임시_상품정보_생성(project, productRepository);
    }

    @Test
    void 쇼핑몰리뷰작성_성공_200() {

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        RequestSpecification request = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,"쇼핑몰 리뷰를 작성합니다. 필수값이 입력되지 않았을 경우 400이 반환됩니다.",
                        "쇼핑몰리뷰작성", UserDocument.AccessTokenHeader,
                        ShoppingMallReviewDocument.ShoppingMallReviewWriteRequestField,
                        ShoppingMallReviewDocument.shoppingMallReviewResponseField))
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
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getString("content")).isEqualTo(rightReviewText);
        assertThat(jsonPath.getInt("score")).isEqualTo(starScore);
        assertThat(jsonPath.getString("productUrl")).isEqualTo(productURL);

        //  verify(s3Client).putObject(Mockito.any(PutObjectRequest.class));
    }

    @Test
    void 쇼핑몰리뷰작성_헤더정보없음_401() {

        RequestSpecification request = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("productURL", productURL)
                .multiPart(CommonSteps.multipartText("content",rightReviewText))
                .multiPart("score", starScore);

        List<MultiPartSpecification> multiPartSpecList = 리뷰_이미지_파일정보_생성();

        for(MultiPartSpecification multiPartSpecification : multiPartSpecList){
            request.multiPart(multiPartSpecification);
        }

        request
                .when()
                .post("/reviews/shopping")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all().extract();
    }

    @Test
    void 쇼핑몰리뷰정보_제품별찾기_200() {
        // 먼저 쇼핑몰리뷰를 보내기
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        쇼핑몰_리뷰_등록(token,5);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "쇼핑몰 제품의 리뷰 전체 요약 정보를 반환합니다. product URL 이 입력되지 않았을 경우 400 이 반환됩니다."
                        ,"쇼핑몰 리뷰 요약정보"
                        ,ShoppingMallReviewDocument.ReviewProductRequestHeader,ShoppingMallReviewDocument.shoppingMallReviewInfoResponseField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("productURL", productURL)
                .when()
                .get("/reviews/shopping")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("averageStarScore", notNullValue())
                .body("totalReviewCount", notNullValue())
                .body("recentReviewCount", notNullValue())
                .body("starScoreArray", notNullValue())
                .log().all();
    }

    @Test
    void 쇼핑몰리뷰정보_제품별찾기_제품등록안됨_202(){
        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("productURL", wrongProductUrl)
                .when()
                .get("/reviews/shopping")
                .then()
                .assertThat()
                .statusCode(HttpStatus.ACCEPTED.value())
                .log().all();
    }

    @Test
    void 쇼핑몰리뷰정보_리뷰리스트_반환_200() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        쇼핑몰_리뷰_등록(token, starScore);
        쇼핑몰_리뷰_등록(token, 5);

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "원하는 제품의 쇼핑몰 리뷰 리스트를 반환합니다." +
                                "<br>product URL 이 입력되지 않았을 경우 400 이 반환됩니다." +
                                "<br>sort 값은 parameter 에 입력해야 하며, 허용된 단어 외 입력 시 400 Bad Request 가 반환됩니다. (BEST, NEWEST)",
                        "쇼핑몰 리뷰 리스트",
                        ShoppingMallReviewDocument.ReviewProductRequestHeader,
                        CommonDocument.OptionalAccessTokenHeader,
                        ShoppingMallReviewDocument.shoppingMallReviewSortField,
                        ShoppingMallReviewDocument.shoppingMallReviewListResponseField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("productURL", productURL)
                .header("X-AUTH-TOKEN", token)
                .param("sort", "BEST")
                .when()
                .get("/reviews/shopping/list")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$", notNullValue())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getList("").size()).isEqualTo(2);
        int firstReviewScore = jsonPath.getInt("[0].score");
        int secondReviewScore = jsonPath.getInt("[1].score");
        assertThat(firstReviewScore).isGreaterThan(secondReviewScore);
    }

    @Test
    void 쇼핑몰_리뷰_삭제_200() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        쇼핑몰_리뷰_등록(token, starScore);
        long reviewId = 리뷰아이디_추출();

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "입력한 리뷰 아이디의 리뷰를 삭제 처리합니다." +
                        "<br>입력된 아이디로 등록된 리뷰가 없는 경우 404 Not Found 가 리턴됩니다." +
                        "<br>해당 리뷰를 수정할 권한이 없는 경우, 401 Unauthorized 가 리턴됩니다", "쇼핑몰리뷰삭제",
                        UserDocument.AccessTokenHeader,
                        ShoppingMallReviewDocument.ReviewIdField,
                        ShoppingMallReviewDocument.shoppingMallReviewResponseField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .pathParam("reviewId", reviewId)
                .when()
                .delete("/reviews/shopping/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getString("status")).isEqualTo("DELETED");
    }

    @Test
    void 쇼핑몰리뷰삭제_헤더정보없음_401() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        쇼핑몰_리뷰_등록(token, starScore);
        long reviewId = 리뷰아이디_추출();

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .pathParam("reviewId", reviewId)
                .when()
                .delete("/reviews/shopping/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all().extract();
    }

    @Test
    void 쇼핑몰리뷰삭제_삭제권한없음_403() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        쇼핑몰_리뷰_등록(token, starScore);
        long reviewId = 리뷰아이디_추출();

        String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .header("X-AUTH-TOKEN", otherToken)
                .pathParam("reviewId",reviewId)
                .when()
                .delete("/reviews/shopping/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all().extract();
    }

    @Test
    void 쇼핑몰_리뷰_별점_수정_200() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        쇼핑몰_리뷰_등록(token, starScore);
        long reviewId = 리뷰아이디_추출();

        long newScore = 5;

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "쇼핑몰 리뷰를 수정하는 API 기능입니다. " +
                                "<br>고치지 않을 부분은 입력하지 않으면 고쳐지지 않으며, 고쳐지는 부분은 작성때와 똑같은 규칙을 작용받습니다" +
                                "<br>X-AUTH-TOKEN 을 입력해야 하며, 유저가 해당 리뷰를 작성하지 않아 리뷰를 수정/삭제할 권한이 존재하지 않는 경우 401 에러를 반환합니다." +
                                "<br>등록되지 않은 리뷰 아이디를 입력했다면, 404 에러를 반환합니다." +
                                "<br>기존의 사진을 삭제하고 싶다면, 조회했을 때 들어간 리뷰 사진의 이름을 multipart form 에 이름을 하나하나 담아 넘겨주면 삭제 처리 됩니다." +
                                "<br>수정이 성공적으로 이루어졌다면 200 OK 가 반환됩니다.", "쇼핑몰리뷰수정", UserDocument.AccessTokenHeader,
                        ShoppingMallReviewDocument.ReviewIdField,
                        ShoppingMallReviewDocument.scoreField,
                        ShoppingMallReviewDocument.shoppingMallReviewResponseField))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", token)
                .pathParam("reviewId", reviewId)
                .multiPart("score", newScore)
                .when()
                .patch("reviews/shopping/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getInt("score")).isEqualTo(newScore);
    }

    @Test
    void 쇼핑몰_리뷰_사진추가_수정_200() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        쇼핑몰_리뷰_등록(token, starScore);
        long reviewId = 리뷰아이디_추출();

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, UserDocument.AccessTokenHeader,
                        ShoppingMallReviewDocument.ReviewIdField, ShoppingMallReviewDocument.imageFileFiend,
                        ShoppingMallReviewDocument.shoppingMallReviewResponseField))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", token)
                .pathParam("reviewId", reviewId)
                .multiPart(리뷰_이미지_파일정보_생성().get(0))
                .when()
                .patch("reviews/shopping/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getList("reviewImageUrlList").size()).isEqualTo(2);
    }

    @Test
    void 쇼핑몰_리뷰_사진삭제_수정_200() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        쇼핑몰_리뷰_등록(token, starScore);
        long reviewId = 리뷰아이디_추출();
        List<String> fileNameList = fileManagerRepository.getRealFilename(reviewId, ReferenceType.REVIEW);

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, UserDocument.AccessTokenHeader,
                        ShoppingMallReviewDocument.ReviewIdField, ShoppingMallReviewDocument.deleteFileListField,
                        ShoppingMallReviewDocument.shoppingMallReviewResponseField))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", token)
                .pathParam("reviewId", reviewId)
                .multiPart("deleteFileList", fileNameList.get(0))
                .when()
                .patch("reviews/shopping/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getList("reviewImageUrlList").size()).isEqualTo(0);
    }

    @Test
    void 쇼핑몰_리뷰_수정_토큰정보없음_401() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        쇼핑몰_리뷰_등록(token, starScore);
        long reviewId = 리뷰아이디_추출();

        List<String> fileNameList = fileManagerRepository.getRealFilename(reviewId, ReferenceType.REVIEW);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .pathParam("reviewId", reviewId)
                .multiPart("deleteFileList", fileNameList.get(0))
                .when()
                .patch("reviews/shopping/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all().extract();
    }

    @Test
    void 쇼핑몰_리뷰_수정_유저권한없음_403() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        쇼핑몰_리뷰_등록(token, starScore);
        long reviewId = 리뷰아이디_추출();

        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());
        List<String> fileNameList = fileManagerRepository.getRealFilename(reviewId, ReferenceType.REVIEW);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .header("X-AUTH-TOKEN", otherToken)
                .pathParam("reviewId", reviewId)
                .multiPart("deleteFileList", fileNameList.get(0))
                .when()
                .patch("reviews/shopping/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all().extract();
    }

    private JsonPath 리뷰리스트_JSONPath_추출() {
        ExtractableResponse<Response> response = given(this.spec)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("productURL",productURL)
                .param("sort","NEWEST")
                .when()
                .get("/reviews/shopping/list")
                .then()
                .log().all().extract();

        return response.jsonPath();
    }

    private long 리뷰아이디_추출(){

        JsonPath jsonPath = 리뷰리스트_JSONPath_추출();
        return jsonPath.getLong("[0].reviewId");
    }

    private Long 쇼핑몰_리뷰_등록(String token, int starScore) {

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


    @Test
    void 쇼핑몰리뷰작성_별점미입력_400(){

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec).log().all()
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", token)
                .multiPart("productURL", productURL)
                .multiPart("content",rightReviewText)
                .when()
                .post("/reviews/shopping")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("find{it.errorType == 'NotBlank' " +
                        "&& it.fieldName == 'score' " + "&& it.message == '별점이 입력되지 않았습니다.'}", notNullValue())
                .log().all().extract();

    //    verify(s3Client,Mockito.times(0)).putObject(Mockito.any(PutObjectRequest.class));
    }

    @Test
    void 쇼핑몰리뷰작성_리뷰10자미만_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec).log().all()
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", token)
                .multiPart("productURL", productURL)
                .multiPart("content",wrongReviewText)
                .multiPart("score", starScore)
                .when()
                .post("/reviews/shopping")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("find{it.errorType == 'Size' " +
                        "&& it.fieldName == 'content' " + "&& it.message == '리뷰내용은 10자 이상이어야 합니다.'}", notNullValue())
                .log().all().extract();

    //    verify(s3Client,Mockito.times(0)).putObject(Mockito.any(PutObjectRequest.class));
    }

    @Test
    void 쇼핑몰리뷰작성_이미지이외파일_400() throws IOException {

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        RequestSpecification request = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,"쇼핑몰 리뷰를 올바로 작성한 부분입니다."))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", token)
                .multiPart("productURL", productURL)
                .multiPart("content",rightReviewText)
                .multiPart("score", starScore);


        List<MultiPartSpecification> multiPartSpecList = 리뷰_이미지아닌_파일정보_생성();

        for(MultiPartSpecification multiPartSpecification : multiPartSpecList){
            request.multiPart(multiPartSpecification);
        }
        request
                .when()
                .post("/reviews/shopping")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("find{it.errorType == 'ImageFiles' " +
                        "&& it.fieldName == 'multipartImageFiles' " + "&& it.message == '입력된 파일이 이미지가 아닙니다.'}", notNullValue())
                .log().all().extract();

    //    verify(s3Client,Mockito.times(0)).putObject(Mockito.any(PutObjectRequest.class));
    }

    @Test
    void 쇼핑몰리뷰작성_유효하지않은토큰_401(){
        final String wrongToken = "wrongToken";

        given(this.spec).log().all()
                .filter(document(DEFAULT_RESTDOC_PATH))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", wrongToken)
                .multiPart("productURL", productURL)
                .multiPart("content",rightReviewText)
                .multiPart("score", starScore)
                .when()
                .post("/reviews/shopping")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all().extract();
    }

    @Test
    void 쇼핑몰리뷰_좋아요_등록_성공_200(){

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long reviewId = 쇼핑몰_리뷰_등록(token, starScore);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "쇼핑몰 리뷰에 좋아요를 추가하는 API 입니다." +
                        "<br>X-AUTH-TOKEN 을 header 에 추가하고, 리뷰의 id 를 path parameter 로 전달합니다." +
                        "<br>좋아요 성공 시 200 OK 와 함께 처리 내역을 반환합니다." +
                        "<br>이미 좋아요를 누른 리뷰에 대해 좋아요를 누르면 좋아요가 취소됩니다." +
                        "<br>리뷰 아이디에 음수를 입력하면 400 Bad Request 가 반환됩니다." +
                        "<br>헤더에 유저 정보를 입력하지 않을 시, 401 Unauthorized 가 반환됩니다." +
                        "<br>존재하지 않는 리뷰 아이디를 입력할 경우, 404 Not Found 가 반환됩니다.", "쇼핑몰리뷰공감",
                        CommonDocument.AccessTokenHeader,
                        ShoppingMallReviewDocument.ReviewIdField,
                        SnsReviewDocument.SnsReactionResponseField))
                .header("X-AUTH-TOKEN", token)
                .pathParam("reviewId", reviewId)
                .when()
                .post("/reviews/shopping/like/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        Review review = reviewRepository.findById(reviewId).get();
        assertThat(review.getReactionCount()).isEqualTo(1);

        boolean isLiked = 쇼핑몰리뷰_리스트_맨첫번째_좋아요여부_가져오기(token);
        assertThat(isLiked).isTrue();
    }

    @Test
    void 쇼핑몰리뷰_좋아요_다시클릭_취소_성공_200(){

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long reviewId = 쇼핑몰_리뷰_등록(token, starScore);
        쇼핑몰리뷰_좋아요_등록(token, reviewId);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        CommonDocument.AccessTokenHeader,
                        ShoppingMallReviewDocument.ReviewIdField,
                        SnsReviewDocument.SnsReactionResponseField))
                .header("X-AUTH-TOKEN", token)
                .pathParam("reviewId", reviewId)
                .when()
                .post("/reviews/shopping/like/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        Review review = reviewRepository.findById(reviewId).get();
        assertThat(review.getReactionCount()).isEqualTo(0);

        boolean isLiked = 쇼핑몰리뷰_리스트_맨첫번째_좋아요여부_가져오기(token);
        assertThat(isLiked).isFalse();
    }
    @Test
    void 쇼핑몰리뷰_좋아요_리뷰아이디음수_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .pathParam("reviewId", -1)
                .when()
                .post("/reviews/shopping/like/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all().extract();
    }
    @Test
    void 쇼핑몰리뷰_좋아요_헤더정보없음_401(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long reviewId = 쇼핑몰_리뷰_등록(token, starScore);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .pathParam("reviewId", reviewId)
                .when()
                .post("/reviews/shopping/like/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all().extract();
    }

    @Test
    void 쇼핑몰리뷰_좋아요_해당리뷰없음_404(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN", token)
                .pathParam("reviewId", 9999L)
                .when()
                .post("/reviews/shopping/like/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all().extract();
    }

    void 쇼핑몰리뷰_좋아요_등록(String token, Long reviewId){
        given(this.spec)
                .header("X-AUTH-TOKEN", token)
                .pathParam("reviewId", reviewId)
                .when()
                .post("/reviews/shopping/like/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();
    }

    boolean 쇼핑몰리뷰_리스트_맨첫번째_좋아요여부_가져오기(String token){
        ExtractableResponse<Response> response = given(this.spec)
                .header("X-AUTH-TOKEN", token)
                .header("productURL", "http://www.example.com/123")
                .param("sort","NEWEST")
                .when()
                .get("/reviews/shopping/list")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        return jsonPath.getBoolean("[0].isLiked");
    }
}
