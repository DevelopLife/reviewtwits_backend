package com.developlife.reviewtwits.review;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.CommonDocument;
import com.developlife.reviewtwits.CommonSteps;
import com.developlife.reviewtwits.entity.*;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.message.response.review.CommentResponse;
import com.developlife.reviewtwits.message.response.sns.DetailSnsReviewResponse;
import com.developlife.reviewtwits.repository.*;
import com.developlife.reviewtwits.repository.CommentRepository;
import com.developlife.reviewtwits.repository.review.ReviewRepository;
import com.developlife.reviewtwits.service.user.UserService;
import com.developlife.reviewtwits.type.review.ReviewStatus;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.developlife.reviewtwits.review.SnsReviewSteps.*;
import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;

/**
 * @author WhalesBob
 * @since 2023-03-31
 */
public class SnsReviewApiTest extends ApiTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserSteps userSteps;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReviewScrapRepository reviewScrapRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private ReactionRepository reactionRepository;

//    @Autowired
//    private AmazonS3 s3Client;

    private RegisterUserRequest registerUserRequest;
    private RegisterUserRequest registerOtherUserRequest;
    @Autowired
    private SnsReviewSteps snsReviewSteps;

    @BeforeEach
    void settings(){
        registerUserRequest = userSteps.회원가입정보_생성();
        userService.register(registerUserRequest, UserSteps.일반유저권한_생성());

        registerOtherUserRequest = userSteps.상대유저_회원가입정보_생성();
        userService.register(registerOtherUserRequest, UserSteps.일반유저권한_생성());
    }


    @Test
    void SNS_리뷰_작성_성공() {

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        RequestSpecification request = given(this.spec)
                .config(config().encoderConfig(encoderConfig()
                        .encodeContentTypeAs("multipart/form-data", ContentType.MULTIPART)
                        .defaultContentCharset("UTF-8")))
                .filter(document(DEFAULT_RESTDOC_PATH, "SNS 리뷰 작성에 대한 API 입니다." +
                        "<br> X-AUTH-TOKEN 을 Header 로 받고, 아래 조건을 모두 맞추면 200 OK 와 함께 SNS 리뷰가 저장됩니다." +
                        "<br> X-AUTH-TOKEN 이 들어가지 않는다면 401 Unauthorized 를 받게 됩니다." +
                        "<br> productURL 은 필수 값이며, http 혹은 https 로 시작하는 url 형식이어야 합니다." +
                        "<br> content 또한 필수 값이며, 10 자 이상으로 작성해야 합니다." +
                        "<br> score(별점) 또한 필수 값이며, 0~5 점 사이로 입력할 수 있습니다." +
                        "<br> productName 도 필수 값입니다. " +
                        "<br> SNS 리뷰에서는 이미지 파일이 필수입니다." +
                        "<br> 필수 값들이 입력되지 않았다면, 400 Bad Request 가 반환되며, 리뷰가 저장되지 않습니다,",
                        "SNS리뷰작성", UserDocument.AccessTokenHeader,
                        SnsReviewDocument.SnsReviewWriteRequestField,
                        SnsReviewDocument.SnsReviewResultResponseField))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", token)
                .multiPart("productURL", productURL)
                .multiPart(CommonSteps.multipartText("content", rightReviewText))
                .multiPart("score", starScore)
                .multiPart(CommonSteps.multipartText("productName",productName));


        List<MultiPartSpecification> multiPartSpecList = 리뷰_이미지_파일정보_생성();

        for(MultiPartSpecification multiPartSpecification : multiPartSpecList){
            request.multiPart(multiPartSpecification);
        }

        ExtractableResponse<Response> response = request.when()
                .post("/sns/reviews")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getString("content")).isEqualTo(rightReviewText);
        assertThat(jsonPath.getString("productName")).isEqualTo(productName);

        Review actualReviewData = reviewRepository.findById(jsonPath.getLong("reviewId")).get();
        assertThat(actualReviewData.getReviewImageCount()).isEqualTo(1);
    }

    @Test
    void SNS_리뷰작성_별점미입력_400(){

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec).log().all()
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", token)
                .multiPart("productURL", productURL)
                .multiPart(CommonSteps.multipartText("content", rightReviewText))
                .multiPart(CommonSteps.multipartText("productName", productName))
                .when()
                .post("/sns/reviews")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("find{it.errorType == 'NotBlank' " +
                        "&& it.fieldName == 'score' " + "&& it.message == '별점이 입력되지 않았습니다.'}", notNullValue())
                .log().all().extract();

        List<Review> registeredReview = reviewRepository.findReviewsByProductUrl(productURL);
        assertThat(registeredReview.size()).isEqualTo(0);
    //    verify(s3Client,Mockito.times(0)).putObject(Mockito.any(PutObjectRequest.class));
    }

    @Test
    void SNS_리뷰작성_리뷰10자미만_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec).log().all()
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", token)
                .multiPart("productURL", productURL)
                .multiPart(CommonSteps.multipartText("content",wrongReviewText))
                .multiPart("score", starScore)
                .multiPart(CommonSteps.multipartText("productName", productName))
                .when()
                .post("/sns/reviews")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("find{it.errorType == 'Size' " +
                        "&& it.fieldName == 'content' " + "&& it.message == '리뷰내용은 10자 이상이어야 합니다.'}", notNullValue())
                .log().all().extract();

        List<Review> registeredReview = reviewRepository.findReviewsByProductUrl(productURL);
        assertThat(registeredReview.size()).isEqualTo(0);
    //    verify(s3Client,Mockito.times(0)).putObject(Mockito.any(PutObjectRequest.class));
    }

    @Test
    void SNS_리뷰작성_이미지이외파일_400() throws IOException {

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        RequestSpecification request = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,CommonDocument.ErrorResponseFields))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", token)
                .multiPart("productURL", productURL)
                .multiPart(CommonSteps.multipartText("content",rightReviewText))
                .multiPart("score", starScore)
                .multiPart(CommonSteps.multipartText("productName", productName));

        List<MultiPartSpecification> multiPartSpecList = 리뷰_이미지아닌_파일정보_생성();

        for(MultiPartSpecification multiPartSpecification : multiPartSpecList){
            request.multiPart(multiPartSpecification);
        }
        request
                .when()
                .post("/sns/reviews")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("find{it.errorType == 'ImageFiles' " +
                        "&& it.fieldName == 'multipartImageFiles' " + "&& it.message == '입력된 파일이 이미지가 아닙니다.'}", notNullValue())
                .log().all().extract();

        List<Review> registeredReview = reviewRepository.findReviewsByProductUrl(productURL);
        assertThat(registeredReview.size()).isEqualTo(0);
    //    verify(s3Client,Mockito.times(0)).putObject(Mockito.any(PutObjectRequest.class));
    }

    @Test
    void SNS_리뷰작성_제품명미입력_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        RequestSpecification request = given(this.spec).log().all()
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", token)
                .multiPart("productURL", productURL)
                .multiPart(CommonSteps.multipartText("content", rightReviewText))
                .multiPart("score", starScore);

        List<MultiPartSpecification> multiPartSpecList = 리뷰_이미지_파일정보_생성();

        for(MultiPartSpecification multiPartSpecification : multiPartSpecList){
            request.multiPart(multiPartSpecification);
        }
        request
                .when()
                .post("/sns/reviews")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("find{it.errorType == 'NotBlank' " +
                        "&& it.fieldName == 'productName' " + "&& it.message == '제품 이름이 입력되지 않았습니다.'}", notNullValue())
                .log().all().extract();

        List<Review> registeredReview = reviewRepository.findReviewsByProductUrl(productURL);
        assertThat(registeredReview.size()).isEqualTo(0);
    //    verify(s3Client,Mockito.times(0)).putObject(Mockito.any(PutObjectRequest.class));
    }

    @Test
    void SNS_리뷰_피드_200() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());
        List<Long> reviewIdList = new ArrayList<>();

        for(int writeCount = 1; writeCount <= 3; writeCount++){
            Long reviewId = snsReviewSteps.SNS_리뷰_작성(token, "review count : " + writeCount);
            reviewIdList.add(reviewId);
        }

        SNS_리액션_추가(token, reviewIdList.get(2));
        SNS_리액션_추가(otherToken, reviewIdList.get(2));

        int size = 2;
        ExtractableResponse<Response> firstResponse = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "SNS 리뷰 피드를 요청하는 API 입니다." +
                        "<br> X-AUTH-PATH 를 넣어주지 않으면, 로그인 처리가 되지 않아 리액션에서 isReacted 가 모두 false 로 나타나게 됩니다." +
                        "<br> 처음에는 reviewId 를 넣어주지 않은 상태로 요청을 보내면 됩니다. 이 때, 가장 최근의 리뷰부터 나타나게 됩니다." +
                        "<br> size 값은 몇 개의 review 를 받을지 나타내는 부분으로써, 필수 값입니다. 넣어주지 않으면, 400 Bad Request 를 마주할 수 있습니다." +
                        "<br> 무한 스크롤을 구현하기 위해, 이후의 review 요청에서는 리뷰 리스트의 마지막 부분의 reviewId 를 넘겨주어야 합니다." +
                        "<br> 이 때, 남은 리뷰의 갯수가 요청한 size 보다 작을 경우, 남은 review 를 모두 넘겨주게 됩니다. " +
                        "<br>마지막 리뷰에서 다시 한번 요청을 보내게 되면, 204 No Content 가 반환됩니다." +
                        "<br> 위 상황에서는 review List 의 크기가 size 보다 작을 수 있습니다.", "SNS리뷰피드요청",
                        SnsReviewDocument.AccessTokenHeader, SnsReviewDocument.ReviewIdAndSizeField,SnsReviewDocument.SnsReviewFeedResponseField))
                .param("size", size)
                .when()
                .get("/sns/feeds")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath firstJsonPath = firstResponse.jsonPath();
        assertThat(firstJsonPath.getList("").size()).isEqualTo(size);

        long reviewIdForSecondRequest = firstJsonPath.getLong("[1].reviewId");

        for(int writeCount = 4; writeCount <= 5; writeCount++){
            snsReviewSteps.SNS_리뷰_작성(token, "review count : " + writeCount);
        }

        ExtractableResponse<Response> secondResponse = given(this.spec)
                .param("size", size)
                .param("reviewId",reviewIdForSecondRequest)
                .when()
                .get("/sns/feeds")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath secondJsonPath = secondResponse.jsonPath();
        assertThat(secondJsonPath.getList("").size()).isEqualTo(1);
    }

    @Test
    void SNS_리뷰_피드_요청마지막_204(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        for(int writeCount = 1; writeCount <= 2; writeCount++){
            snsReviewSteps.SNS_리뷰_작성(token, "review count : " + writeCount);
        }

        int size = 2;
        ExtractableResponse<Response> feedResponse = given(this.spec)
                .header("X-AUTH-TOKEN", token)
                .param("size", size)
                .when()
                .get("/sns/feeds")
                .then()
                .assertThat()
                .log().all().extract();

        JsonPath feedPath = feedResponse.jsonPath();
        long lastReviewId = feedPath.getLong("[1].reviewId");

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .header("X-AUTH-TOKEN", token)
                .param("size", size)
                .param("reviewId", lastReviewId)
                .when()
                .get("/sns/feeds")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all().extract();
    }

    @Test
    void SNS_리뷰_하나요청_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());
        List<Long> reviewIdList = new ArrayList<>();
        for(int writeCount = 1; writeCount <= 3; writeCount++){
            Long reviewId = snsReviewSteps.SNS_리뷰_작성(token, "review count : " + writeCount);
            reviewIdList.add(reviewId);
        }

        SNS_리액션_추가(token, reviewIdList.get(0));
        SNS_리액션_추가(otherToken, reviewIdList.get(0));

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "SNS 리뷰 하나를 요청하는 API 입니다." +
                                "<br>X-AUTH-PATH 를 넣어주지 않으면, 로그인 처리가 되지 않아 리액션에서 isReacted 가 모두 false 로 나타나게 됩니다." +
                                "<br>reviewId 는 필수값입니다. 리뷰 아이디를 입력하지 않으면 404가 반환됩니다." +
                                "<br>reviewId 로 된 리뷰를 찾을 수 없을 때, 404 Not Found 가 반환됩니다.", "SNS리뷰하나요청",
                        SnsReviewDocument.AccessTokenHeader, SnsReviewDocument.ReviewIdField,SnsReviewDocument.SnsReviewResultResponseField))
                .header("X-AUTH-TOKEN",token)
                .pathParam("reviewId",reviewIdList.get(0))
                .when()
                .get("/sns/reviews/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getList("reviewImageUrlList").size()).isEqualTo(2);
        assertThat(jsonPath.getInt("reactionResponses.GOOD.count")).isEqualTo(2);
    }
    @Test
    void SNS_리뷰_해당리뷰없음_404(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        long wrongReviewId = 999999999L;

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN",token)
                .pathParam("reviewId",wrongReviewId)
                .when()
                .get("/sns/reviews/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all().extract();
    }

    @Test
    void SNS_리뷰_수정_200() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long recentReviewId = SNS_리뷰_작성(token, "write review for comment test");

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "SNS 리뷰를 수정하는 API 입니다. " +
                                "<br> X-AUTH-TOKEN 을 Header 로 받고, 아래 조건을 모두 맞추면 200 OK 와 함께 SNS 리뷰가 수정됩니다." +
                                "<br> X-AUTH-TOKEN 이 들어가지 않았거나, 올바른 값이 아니라면 401 Unauthorized 를 받게 됩니다." +
                                "<br> 해당 유저가 리뷰를 수정할 권한이 없다면 403 Forbidden 이 반환됩니다." +
                                "<br> content 는 선택적으로 입력할 수 있으며, 10 자 이상으로 작성해야 합니다." +
                                "<br> score(별점) 또한 선택 값이며, 0~5 점 사이로 입력할 수 있습니다." +
                                "<br> productName 도 선택 값입니다. " +
                                "<br> SNS 리뷰에서는 이미지 파일이 선택 값입니다. 이미지 확장자로 된 파일을 넣어야 합니다." +
                                "<br> 기존의 사진을 삭제하고 싶다면, 조회했을 때 들어간 리뷰 사진의 이름을 multipart form 에 이름을 하나하나 담아 넘겨주면 삭제 처리 됩니다." +
                                "<br> 파일 삭제를 위한 리스트의 이름은 반드시 이미지 확장자로 끝나야 합니다." +
                                "<br> 위의 조건이 충족되지 않으면, 400 Bad Request 가 반환되며, 리뷰가 수정되지 않습니다." +
                                "<br> 수정하려는 리뷰가 존재하지 않는다면, 404 Not Found 가 반환됩니다."
                        , "SNS리뷰수정", UserDocument.AccessTokenHeader,
                        SnsReviewDocument.SnsReviewChangeRequestField,
                        SnsReviewDocument.SnsReviewResultResponseField))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", token)
                .pathParam("reviewId", recentReviewId)
                .multiPart(CommonSteps.multipartText("content", changeCommentContent))
                .multiPart("score", 3)
                .when()
                .patch("/sns/reviews/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        Review changedReview = reviewRepository.findById(recentReviewId).get();
        assertThat(changedReview.getContent()).isEqualTo(changeCommentContent);
        assertThat(changedReview.getScore()).isEqualTo(3);

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getString("content")).isEqualTo(changeCommentContent);
        assertThat(jsonPath.getLong("score")).isEqualTo(3);
    }

    @Test
    void SNS_리뷰_수정_토큰정보없음_401(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long recentReviewId = SNS_리뷰_작성(token, "write review for comment test");

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .pathParam("reviewId", recentReviewId)
                .multiPart(CommonSteps.multipartText("content", changeCommentContent))
                .multiPart("score", 3)
                .when()
                .patch("/sns/reviews/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all().extract();
    }

    @Test
    void SNS_리뷰_수정_유저권한없음_403(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long recentReviewId = SNS_리뷰_작성(token, "write review for comment test");

        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", otherToken)
                .pathParam("reviewId", recentReviewId)
                .multiPart(CommonSteps.multipartText("content", changeCommentContent))
                .multiPart("score", 3)
                .when()
                .patch("/sns/reviews/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all().extract();
    }

    @Test
    void SNS_리뷰_삭제_200() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long recentReviewId = SNS_리뷰_작성(token, "write review for comment test");

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "SNS 리뷰를 삭제하는 API 입니다. " +
                                "<br> X-AUTH-TOKEN 을 Header 로 받고, 아래 조건을 모두 맞추면 200 OK 와 함께 SNS 리뷰가 삭제됩니다." +
                                "<br> X-AUTH-TOKEN 이 들어가지 않았거나, 올바르지 않은 토큰일 경우 401 Unauthorized 를 받게 됩니다." +
                                "<br> 해당 유저가 리뷰를 삭제할 권한을 가지지 못했다면, 403 Forbidden 이 반환됩니다." +
                                "<br> 삭제하려는 리뷰가 존재하지 않는다면, 404 Not Found 가 반환됩니다."
                        , "SNS리뷰삭제", UserDocument.AccessTokenHeader,
                        SnsReviewDocument.ReviewIdField,
                        SnsReviewDocument.SnsReviewResultResponseField))
                .header("X-AUTH-TOKEN", token)
                .pathParam("reviewId", recentReviewId)
                .when()
                .delete("/sns/reviews/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        Review deletedReview = reviewRepository.findById(recentReviewId).get();
        assertThat(deletedReview.getStatus()).isEqualTo(ReviewStatus.DELETED);
    }

    @Test
    void SNS_리뷰_삭제_토큰정보없음_401() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long recentReviewId = SNS_리뷰_작성(token, "write review for comment test");

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .pathParam("reviewId", recentReviewId)
                .when()
                .delete("/sns/reviews/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all().extract();
    }

    @Test
    void SNS_리뷰_삭제_유저권한없음_403() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long recentReviewId = SNS_리뷰_작성(token, "write review for comment test");

        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .header("X-AUTH-TOKEN", otherToken)
                .pathParam("reviewId", recentReviewId)
                .when()
                .delete("/sns/reviews/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all().extract();
    }


    @Test
    void SNS_리뷰_댓글작성_200() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long recentReviewId = snsReviewSteps.SNS_리뷰_작성(token, "write review for comment test");


        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "SNS 리뷰 댓글을 작성하는 API 입니다." +
                        "<br> X-AUTH-TOKEN 은 필수로 Header 에 입력되어야 하며, 입력되지 않았을 경우 401 Unauthorized 가 나타나게 됩니다." +
                        "<br> 리뷰 아이디 또한 필수 값이며, Path 에 함께 입력할 수 있습니다." +
                        "<br> 리뷰 내용도 필수 값입니다." +
                        "<br> parentId 는 필수 값이며, 없을 경우 0을 입력해 주셔야 합니다. 음수를 입력할 수는 없습니다." +
                        "<br> parentId 가 0인 경우, 부모 댓글로 간주받게 되며, parentId 가 0이 아닐 경우, 부모 댓글의 대댓글로 간주됩니다." +
                        "<br> token 을 제외한 위의 규칙이 지켜지지 않을 경우, 400 Bad Request 가 반환됩니다." +
                        "<br> 모든 조건이 지켜진 상태라면, 댓글 저장과 함께 200 OK 가 반환됩니다.","SNS댓글작성",
                        UserDocument.AccessTokenHeader,
                        SnsReviewDocument.ReviewIdField,
                        SnsReviewDocument.SnsCommentWriteRequestField,
                        SnsReviewDocument.SnsCommentResultResponseField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN",token)
                .pathParam("reviewId", recentReviewId)
                .body(SnsReviewSteps.댓글_작성정보_생성())
                .when()
                .post("/sns/comments/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all();

        Review review = reviewRepository.findById(recentReviewId).get();
        assertThat(review.getCommentCount()).isEqualTo(1);

        List<Comment> allComment = commentRepository.findAll();
        assertThat(allComment.size()).isEqualTo(1);
    }

    @Test
    void SNS_리뷰_댓글확인_200() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = snsReviewSteps.SNS_리뷰_작성(token, "write review for comment test");
        SNS_리뷰_댓글_작성(token,registeredReviewId);

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "SNS 리뷰의 댓글을 확인하는 API 입니다." +
                        "<br>유저 헤더 정보 입력은 선택사항입니다. 헤더 정보가 존재하지 않을 시 isCommentLike 가 false 로 반환됩니다." +
                        "<br>리뷰 아이디가 path에 들어가며, 해당 아이디로 된 리뷰가 존재하지 않을 경우 404 Not Found 가 반환됩니다." +
                        "<br>리뷰가 존재한다면, 200 OK 와 함께 리뷰의 댓글 리스트들이 반환됩니다.","SNS리뷰댓글확인"
                        ,SnsReviewDocument.AccessTokenHeader,
                        SnsReviewDocument.ReviewIdField,
                        SnsReviewDocument.SnsReviewCommentResponseField))
                .pathParam("reviewId", registeredReviewId)
                .when()
                .get("/sns/comments/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();


        JsonPath jsonPath = response.jsonPath();
        List<Comment> commentList = jsonPath.getList("");
        assertThat(commentList.size()).isEqualTo(1);

        String extractedCommentContent = jsonPath.getString("[0].content");
        assertThat(extractedCommentContent).isEqualTo(commentContent);
    }

    @Test
    void SNS_리뷰_댓글_삭제_성공_200() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = snsReviewSteps.SNS_리뷰_작성(token, "write review for comment test");
        Long commentId = SNS_리뷰_댓글_작성(token, registeredReviewId);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "SNS 리뷰 댓글을 삭제하는 API 입니다." +
                        "<br>X-AUTH-TOKEN 은 필수값으로 header 에 들어가야 합니다." +
                        "<br>commentId 또한 필수 값이며, path 에 같이 입력할 수 있습니다." +
                        "<br>해당 유저가 댓글을 삭제할 권한이 없다면, 403 Forbidden 이 반환됩니다." +
                        "<br>지우고자 하는 댓글이 존재하지 않으면, 404 Not Found 가 반환됩니다." +
                        "<br>정상적으로 삭제되었다면, 200 OK 가 반환됩니다.", "SNS리뷰댓글삭제"
                        ,UserDocument.AccessTokenHeader, SnsReviewDocument.CommentIdField,
                        SnsReviewDocument.SnsCommentResultResponseField))
                .header("X-AUTH-TOKEN",token)
                .pathParam("commentId", commentId)
                .when()
                .delete("/sns/comments/{commentId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        List<CommentResponse> commentList = commentRepository.findByReview_ReviewId(registeredReviewId, null);
        assertThat(commentList).isEmpty();
    }

    @Test
    void SNS_리뷰_댓글_삭제_토큰정보없음_401() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = snsReviewSteps.SNS_리뷰_작성(token, "write review for comment test");
        Long commentId = SNS_리뷰_댓글_작성(token, registeredReviewId);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .pathParam("commentId", commentId)
                .when()
                .delete("/sns/comments/{commentId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all();
    }

    @Test
    void SNS_리뷰_댓글_삭제_유저권한없음_403() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = snsReviewSteps.SNS_리뷰_작성(token, "write review for comment test");
        Long commentId = SNS_리뷰_댓글_작성(token, registeredReviewId);

        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .header("X-AUTH-TOKEN",otherToken)
                .pathParam("commentId", commentId)
                .when()
                .delete("/sns/comments/{commentId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all();
    }

    @Test
    void SNS_리뷰_댓글_수정_성공_200() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = snsReviewSteps.SNS_리뷰_작성(token, "write review for comment test");
        Long commentId = SNS_리뷰_댓글_작성(token, registeredReviewId);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "SNS 리뷰 댓글을 수정하는 API 입니다." +
                        "<br>X-AUTH-TOKEN 은 필수값으로 header 에 들어가야 합니다." +
                        "<br>commentId 또한 필수 값이며, path 에 같이 입력할 수 있습니다." +
                        "<br>수정하고자 하는 내용은 필수 값이며, Http Request Query 에 작성할 수 있습니다." +
                        "<br>X-AUTH-HEADER 가 존재하지 않거나, 올바르지 않거나, 해당 유저가 댓글을 수정할 권한이 없다면 401 Unauthorized 가 반환됩니다." +
                        "<br>수정할 commentId 가 존재하지 않으면, 404 Not Found 가 반환됩니다." +
                        "<br>댓글 내용이 존재하지 않으면, 400 Bad Request 가 반환됩니다." +
                        "<br>댓글이 성공적으로 수정되었다면 200 OK 가 반환됩니다" +
                        "<br><br> 수정 이후 반환되는 메세지의 isCommentLike 는 정확하지 않을 수 있습니다. 다시 리뷰 댓글 확인 API 를 통해 확인해주세요.","SNS리뷰댓글수정",
                        UserDocument.AccessTokenHeader,
                        SnsReviewDocument.CommentIdField,
                        SnsReviewDocument.SnsCommentChangeRequestField,
                        SnsReviewDocument.SnsCommentResultResponseField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN",token)
                .pathParam("commentId", commentId)
                .param("content",changeCommentContent)
                .when()
                .patch("/sns/comments/{commentId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all();

        Comment modifiedComment = commentRepository.findById(commentId).get();
        assertThat(modifiedComment.getContent()).isEqualTo(changeCommentContent);
    }

    @Test
    void SNS_리뷰_댓글_수정_토큰정보없음_401(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = snsReviewSteps.SNS_리뷰_작성(token, "write review for comment test");
        Long commentId = SNS_리뷰_댓글_작성(token, registeredReviewId);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("commentId", commentId)
                .param("content",changeCommentContent)
                .when()
                .patch("/sns/comments/{commentId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all();
    }

    @Test
    void SNS_리뷰_댓글_수정_유저권한없음_403(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = snsReviewSteps.SNS_리뷰_작성(token, "write review for comment test");
        Long commentId = SNS_리뷰_댓글_작성(token, registeredReviewId);

        final String otherToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", otherToken)
                .pathParam("commentId", commentId)
                .param("content",changeCommentContent)
                .when()
                .patch("/sns/comments/{commentId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all();
    }

    @Test
    void 피드_리액션_추가_성공_200() {

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = snsReviewSteps.SNS_리뷰_작성(token, "write review for comment test");

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "피드 리액션 추가 및 수정을 위한 API 입니다." +
                                "<br>X-AUTH-TOKEN 은 필수 값으로 header 에 들어가야 합니다." +
                                "<br>리액션을 추가할 reviewId 는 필수값이며, path 에 같이 추가될 수 있습니다." +
                                "<br>리액션 내용은 Request Body 에 넣을 수 있습니다." +
                                "<br>리액션 내용으로 넣을 수 있는 요소는 아래와 같습니다." +
                                "<br>LOVE,SUNGLASSES,LAUGHING,SURPRISING,THINKING," +
                                "<br>PLEADING,SHOCKING,PRAYING,GOOD,NOTICING" +
                                "<br><br> 존재하는 리액션에 다시 리액션을 추가하면, 리액션이 수정됩니다." +
                                "<br>이미 완전히 똑같은 리액션의 내용으로 다시 API 를 호출하면, 리액션이 삭제됩니다.",
                        "SNS리뷰리액션"
                        , UserDocument.AccessTokenHeader,
                        SnsReviewDocument.ReviewIdField,
                        SnsReviewDocument.SnsReactionAddRequestField,
                        SnsReviewDocument.SnsReactionResponseField))
                .header("X-AUTH-TOKEN", token)
                .pathParam("reviewId", registeredReviewId)
                .param("reaction", reactionContent)
                .when()
                .post("/sns/review-reaction/{reviewId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getString("reactionType")).isEqualTo(reactionContent);

        User user = userRepository.findById(jsonPath.getLong("userId")).get();
        assertThat(user.getAccountId()).isEqualTo(UserSteps.accountId);

        assertThat(jsonPath.getLong("reviewId")).isEqualTo(registeredReviewId);

        reactionRepository.findById(jsonPath.getLong("reactionId")).ifPresent(reaction -> {
            assertThat(reaction.getReactionType().toString()).isEqualTo(reactionContent);
        });
    }

    @Test
    void 피드_리액션_수정_성공_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = snsReviewSteps.SNS_리뷰_작성(token, "write review for comment test");
        SNS_리액션_추가(token, registeredReviewId);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .header("X-AUTH-TOKEN", token)
                .pathParam("reviewId", registeredReviewId)
                .param("reaction",newReactionContent)
                .when()
                .post("/sns/review-reaction/{reviewId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all();

        JsonPath jsonPath = 피드_리뷰_리액션_추출(token);
        boolean isReacted = jsonPath.getBoolean("[0].reactionResponses."+ newReactionContent + ".isReacted");

        assertThat(isReacted).isEqualTo(true);
    }

    @Test
    void 피드_리액션_삭제_성공_200() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = snsReviewSteps.SNS_리뷰_작성(token, "write review for comment test");
        SNS_리액션_추가(token, registeredReviewId);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        UserDocument.AccessTokenHeader,
                        SnsReviewDocument.ReviewIdField,
                        SnsReviewDocument.SnsReactionResponseField))
                .header("X-AUTH-TOKEN",token)
                .pathParam("reviewId",registeredReviewId)
                .param("reaction",reactionContent)
                .when()
                .post("/sns/review-reaction/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all();

        JsonPath jsonPath = 피드_리뷰_리액션_추출(token);
        assertThat(jsonPath.getMap("[0].reactionResponses")).isEmpty();
    }

    @Test
    void 리뷰_스크랩_추가_성공_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = SNS_리뷰_작성(token, "write review for comment test");

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "리뷰 스크랩 내용을 추가하는 API 입니다." +
                                "<br> X-AUTH-TOKEN 을 header 에 넣어 주고, 존재하는 reviewId 를 path 에 추가하면" +
                                "<br> 200 OK 와 함께 해당 리뷰가 유저에 스크랩됩니다." +
                                "<br> X-AUTH-TOKEN 이 올바르지 않거나 입력되지 않았을 경우 403 에러가 표출되게 됩니다." +
                                "<br> 해당 리뷰가 존재하지 않을 경우, 404 에러가 표출되게 됩니다." +
                                "<br>이미 스크랩한 리뷰의 경우, 409 에러가 표출되게 됩니다.", "리뷰스크랩추가",
                        UserDocument.AccessTokenHeader,
                        SnsReviewDocument.ReviewIdField,
                        SnsReviewDocument.SnsReviewScrapResultResponseField))
                .header("X-AUTH-TOKEN", token)
                .pathParam("reviewId", registeredReviewId)
                .when()
                .post("/sns/scrap-reviews/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        Long foundUserId = jsonPath.getLong("userId");
        Long foundReviewId = jsonPath.getLong("reviewId");

        assertThat(foundReviewId).isEqualTo(registeredReviewId);

        User user = userRepository.findById(foundUserId).get();
        Review review = reviewRepository.findById(foundReviewId).get();
        Optional<ReviewScrap> foundReviewScrap = reviewScrapRepository.findByReviewAndUser(review, user);
        assertThat(foundReviewScrap).isPresent();
    }

    @Test
    void 리뷰_스크랩_추가_유저정보없음_401(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = SNS_리뷰_작성(token, "write review for comment test");

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .pathParam("reviewId",registeredReviewId)
                .when()
                .post("/sns/scrap-reviews/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all();
    }

    @Test
    void 리뷰_스크랩_추가_리뷰존재안함_404(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long wrongReviewId = 9999L;

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN",token)
                .pathParam("reviewId",wrongReviewId)
                .when()
                .post("/sns/scrap-reviews/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("find{it.errorType == 'ReviewNotFoundException' " +
                        "&& it.fieldName == 'reviewId' " + "&& it.message == '스크랩하려는 리뷰 아이디가 존재하지 않습니다.'}", notNullValue())
                .log().all();
    }

    @Test
    void 리뷰_스크랩_추가_이미스크랩한리뷰_409(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = SNS_리뷰_작성(token, "write review for comment test");
        SNS_리뷰_스크랩_추가(token, registeredReviewId);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN",token)
                .pathParam("reviewId",registeredReviewId)
                .when()
                .post("/sns/scrap-reviews/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("find{it.errorType == 'ReviewScrapConflictException' "
                        + "&& it.message == '이미 등록된 리뷰 스크랩입니다.'}", notNullValue())
                .log().all();
    }

    @Test
    void 리뷰_스크랩_삭제_성공_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = SNS_리뷰_작성(token, "write review for comment test");
        SNS_리뷰_스크랩_추가(token, registeredReviewId);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "리뷰 스크랩을 취소하는 API 입니다." +
                        "<br> X-AUTH-TOKEN 을 넣어 주고, 이미 스크랩한 리뷰 아이디를 넣어 주면 200 OK 와 함깨 해당 스크랩이 취소됩니다." +
                        "<br> X-AUTH-TOKEN 이 올바르지 않거나 입력되지 않았을 경우 403 에러가 표출되게 됩니다." +
                        "<br> 해당 리뷰가 존재하지 않을 경우, 404 에러가 표출되게 됩니다." +
                        "<br> 해당 리뷰가 이미 스크랩되어 있지 않은 경우, 409 에러가 표출되게 됩니다.","리뷰스크랩취소",
                        UserDocument.AccessTokenHeader,
                        SnsReviewDocument.ReviewIdField,
                        SnsReviewDocument.SnsReviewScrapResultResponseField))
                .header("X-AUTH-TOKEN",token)
                .pathParam("reviewId",registeredReviewId)
                .when()
                .delete("/sns/scrap-reviews/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all();

        List<ReviewScrap> reviewScrapList = reviewScrapRepository.findAll();
        assertThat(reviewScrapList).isEmpty();
    }

    @Test
    void 리뷰_스크랩_삭제_유저정보없음_401(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = SNS_리뷰_작성(token, "write review for comment test");
        SNS_리뷰_스크랩_추가(token, registeredReviewId);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .pathParam("reviewId",registeredReviewId)
                .when()
                .delete("/sns/scrap-reviews/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all();
    }

    @Test
    void 리뷰_스크랩_삭제_리뷰없음_404(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long wrongReviewId = 9999L;

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN",token)
                .pathParam("reviewId",wrongReviewId)
                .when()
                .delete("/sns/scrap-reviews/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("find{it.errorType == 'ReviewNotFoundException' " +
                        "&& it.fieldName == 'reviewId' " + "&& it.message == '삭제하려는 리뷰가 존재하지 않습니다.'}", notNullValue())
                .log().all();
    }


    @Test
    void 리뷰_스크랩_삭제_이미스크랩안함_409(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = SNS_리뷰_작성(token, "write review for comment test");

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN",token)
                .pathParam("reviewId",registeredReviewId)
                .when()
                .delete("/sns/scrap-reviews/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("find{it.errorType == 'ReviewScrapConflictException' "
                        + "&& it.message == '등록되지 않은 리뷰 스크랩입니다.'}", notNullValue())
                .log().all();
    }

    @Test
    void 유저_리뷰_스크랩정보_찾기_성공_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = SNS_리뷰_작성(token, "write review for comment test");
        SNS_리뷰_스크랩_추가(token, registeredReviewId);

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "유저 정보를 기준으로 스크랩 정보를 찾아 보여주는 API 입니다." +
                        "<br> 올바른 X-AUTH-TOKEN 을 header 에 넣어 주면, 해당 유저가 스크랩한 모든 리뷰 정보가 반환됩니다." +
                        "<br> 올바르지 않은 X-AUTH-TOKEN 을 입력할 경우, 403 에러가 반환됩니다.","리뷰스크랩정보찾기",
                        UserDocument.AccessTokenHeader, SnsReviewDocument.SnsReviewFeedResponseField))
                .header("X-AUTH-TOKEN", token)
                .when()
                .get("/sns/scrap-reviews")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getBoolean("[0].isScrapped")).isTrue();
    }

    @Test
    void 유저_리뷰_스크랩정보_찾기_유저정보없음_401(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = SNS_리뷰_작성(token, "write review for comment test");
        SNS_리뷰_스크랩_추가(token, registeredReviewId);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .when()
                .get("/sns/scrap-reviews")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all();
    }

    @Test
    void 댓글공감_성공_200() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = SNS_리뷰_작성(token, "write review for comment test");
        Long commentId = SNS_리뷰_댓글_작성(token, registeredReviewId);

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "댓글에 공감을 추가하는 API 입니다." +
                                "<br>header 에 X-AUTH-TOKEN 을 넣고, path 에 commentId 를 올바르게 넣어 요청할 수 있습니다." +
                                "<br>header 에 토큰 값을 넣지 않았거나, 올바르지 않은 토큰을 입력했다면 403 Forbidden 이 반환됩니다." +
                                "<br>존재하지 않은 commentId 를 입력했다면 404 Not Found 가 반환됩니다." +
                                "<br>이미 좋아요를 누른 상태에서 다시 요청을 보내면, 409 Conflict 가 반환됩니다." +
                                "<br>모두 올바른 값을 입력했다면, 해당 댓글에 좋아요가 처리되고 200 OK 가 반환됩니다.", "댓글좋아요요청",
                        UserDocument.AccessTokenHeader,
                        SnsReviewDocument.CommentIdField,
                        SnsReviewDocument.SnsCommentLikeResultResponseField))
                .header("X-AUTH-TOKEN", token)
                .pathParam("commentId", commentId)
                .when()
                .post("/sns/comments-like/{commentId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();

        Long foundCommentLikeId = jsonPath.getLong("commentLikeId");

        CommentLike commentLike = commentLikeRepository.findById(foundCommentLikeId).get();
        assertThat(commentLike.getComment().getCommentId()).isEqualTo(commentId);

        JsonPath commentJsonPath = 댓글_JsonPath_불러오기(registeredReviewId, token);
        assertThat(commentJsonPath.getInt("[0].commentLikeCount")).isEqualTo(1);
    }

    @Test
    void 댓글공감_토큰없음_401(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = SNS_리뷰_작성(token, "write review for comment test");
        Long commentId = SNS_리뷰_댓글_작성(token, registeredReviewId);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .pathParam("commentId",commentId)
                .when()
                .post("/sns/comments-like/{commentId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all();
    }

    @Test
    void 댓글공감_댓글없음_404(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long wrongCommentId = 9999L;

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN",token)
                .pathParam("commentId",wrongCommentId)
                .when()
                .post("/sns/comments-like/{commentId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("find{it.errorType == 'CommentNotFoundException' "
                        + "&& it.message == '좋아요를 입력할 comment 가 존재하지 않습니다.'}", notNullValue())
                .log().all();
    }

    @Test
    void 댓글공감_이미공감한댓글_409(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = SNS_리뷰_작성(token, "write review for comment test");
        Long commentId = SNS_리뷰_댓글_작성(token, registeredReviewId);
        SNS_댓글공감_추가(token,commentId);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN",token)
                .pathParam("commentId",commentId)
                .when()
                .post("/sns/comments-like/{commentId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("find{it.errorType == 'CommentLikeAlreadyProcessedException' "
                        + "&& it.message == '이미 해당 댓글에 좋아요를 누르셨습니다.'}", notNullValue())
                .log().all();
    }

    @Test
    void 댓글공감_취소_성공_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = SNS_리뷰_작성(token, "write review for comment test");
        Long commentId = SNS_리뷰_댓글_작성(token, registeredReviewId);
        SNS_댓글공감_추가(token,commentId);

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "댓글에 공감을 취소하는 API 입니다." +
                                "<br>header 에 X-AUTH-TOKEN 을 넣고, path 에 commentId 를 올바르게 넣어 요청할 수 있습니다." +
                                "<br>header 에 토큰 값을 넣지 않았거나, 올바르지 않은 토큰을 입력했다면 403 Forbidden 이 반환됩니다." +
                                "<br>존재하지 않은 commentId 를 입력했다면 404 Not Found 가 반환됩니다." +
                                "<br>이미 좋아요를 추가하지 않았거나 좋아요를 취소한 상태에서 다시 취소 요청을 보내면, 409 Conflict 가 반환됩니다." +
                                "<br>모두 올바른 값을 입력했다면, 해당 댓글에 좋아요가 처리되고 200 OK 가 반환됩니다.", "댓글좋아요취소요청",
                        UserDocument.AccessTokenHeader,
                        SnsReviewDocument.CommentIdField,
                        SnsReviewDocument.SnsCommentLikeResultResponseField))
                .header("X-AUTH-TOKEN", token)
                .pathParam("commentId", commentId)
                .when()
                .delete("/sns/comments-like/{commentId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();

        Long commentLikeId = jsonPath.getLong("commentLikeId");

        User user = userRepository.findByAccountId(UserSteps.accountId).get();
        assertThat(jsonPath.getLong("userId")).isEqualTo(user.getUserId());


        Optional<CommentLike> foundComment = commentLikeRepository.findById(commentLikeId);
        assertThat(foundComment).isNotPresent();

        commentRepository.findById(jsonPath.getLong("commentId")).ifPresent(comment -> {
            assertThat(comment.getCommentLike()).isEqualTo(0);
        });

        JsonPath commentJsonPath = 댓글_JsonPath_불러오기(registeredReviewId, token);
        assertThat(commentJsonPath.getInt("[0].commentLikeCount")).isEqualTo(0);
    }

    @Test
    void 댓글공감_취소_토큰없음_401(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = SNS_리뷰_작성(token, "write review for comment test");
        Long commentId = SNS_리뷰_댓글_작성(token, registeredReviewId);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .pathParam("commentId",commentId)
                .when()
                .delete("/sns/comments-like/{commentId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all();
    }

    @Test
    void 댓글공감_취소_댓글없음_404(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long wrongCommentId = 9999L;

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN",token)
                .pathParam("commentId",wrongCommentId)
                .when()
                .delete("/sns/comments-like/{commentId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("find{it.errorType == 'CommentNotFoundException' "
                        + "&& it.message == '좋아요를 취소할 comment 가 존재하지 않습니다.'}", notNullValue())
                .log().all();
    }

    @Test
    void 댓글공감_취소_이미공감없음_409(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = SNS_리뷰_작성(token, "write review for comment test");
        Long commentId = SNS_리뷰_댓글_작성(token, registeredReviewId);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN",token)
                .pathParam("commentId",commentId)
                .when()
                .delete("/sns/comments-like/{commentId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("find{it.errorType == 'CommentLikeAlreadyProcessedException' "
                        + "&& it.message == '해당 댓글에 좋아요를 누르지 않으셨거나, 이미 취소한 좋아요입니다.'}", notNullValue())
                .log().all();
    }

    @Test
    void 리뷰매핑_시험테스트(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = SNS_리뷰_작성(token, "write review for comment test");
        Long commentId = SNS_리뷰_댓글_작성(token, registeredReviewId);

        SNS_리액션_추가(token, registeredReviewId);
        SNS_리뷰_스크랩_추가(token, registeredReviewId);

        SNS_댓글공감_추가(token,commentId);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("reviewId").descending());

        User user = userRepository.findByAccountId(UserSteps.accountId).get();
        List<DetailSnsReviewResponse> allReviews = reviewRepository.findMappingReviewById(user,null,pageable);
        assertThat(allReviews).isNotEmpty();
    }

    Long SNS_리뷰_작성(String token, String content) {

        RequestSpecification request = given(this.spec).log().all()
                .header("X-AUTH-TOKEN", token)
                .multiPart("productURL", productURL)
                .multiPart(CommonSteps.multipartText("content", content))
                .multiPart("score", starScore)
                .multiPart(CommonSteps.multipartText("productName",productName));

        List<MultiPartSpecification> multiPartSpecList = 리뷰_이미지_파일정보_생성();

        for(MultiPartSpecification multiPartSpecification : multiPartSpecList){
            request.multiPart(multiPartSpecification);
        }

        request.when().post("/sns/reviews")
                .then()
                .log().all();

        List<Review> reviewList = reviewRepository.findReviewsByProductUrl(productURL);
        return reviewList.get(reviewList.size()-1).getReviewId();
    }

    Long SNS_리뷰_댓글_작성(String token, long reviewId){

        ExtractableResponse<Response> response = given(this.spec)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .pathParam("reviewId", reviewId)
                .body(댓글_작성정보_생성())
                .when()
                .post("/sns/comments/{reviewId}")
                .then()
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();

        long commentId = jsonPath.getLong("commentId");

        SNS_댓글공감_추가(token, commentId);
        return commentId;
    }

    JsonPath 피드_리뷰_리액션_추출(String token){

        int size = 2;
        ExtractableResponse<Response> feedResponse = given(this.spec)
                .header("X-AUTH-TOKEN", token)
                .param("size", size)
                .when()
                .get("/sns/feeds")
                .then()
                .assertThat()
                .log().all().extract();

        return feedResponse.jsonPath();
    }

    void SNS_리액션_추가(String token, long registeredReviewId){
        given(this.spec)
                .header("X-AUTH-TOKEN", token)
                .pathParam("reviewId", registeredReviewId)
                .param("reaction",reactionContent)
                .when()
                .post("/sns/review-reaction/{reviewId}")
                .then()
                .log().all();
    }

    void SNS_리뷰_스크랩_추가(String token, Long reviewId){
        given(this.spec)
                .header("X-AUTH-TOKEN",token)
                .pathParam("reviewId",reviewId)
                .when()
                .post("/sns/scrap-reviews/{reviewId}")
                .then()
                .log().all();
    }
    void SNS_댓글공감_추가(String token, Long commentId){
        given(this.spec)
                .header("X-AUTH-TOKEN",token)
                .pathParam("commentId",commentId)
                .when()
                .post("/sns/comments-like/{commentId}")
                .then()
                .log().all();
    }

    JsonPath 댓글_JsonPath_불러오기(Long reviewId, String token){
        ExtractableResponse<Response> response = given(this.spec)
                .header("X-AUTH-TOKEN",token)
                .pathParam("reviewId", reviewId)
                .when()
                .get("/sns/comments/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        return response.jsonPath();
    }
}