package com.developlife.reviewtwits.review;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.CommonDocument;
import com.developlife.reviewtwits.entity.Comment;
import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.repository.CommentRepository;
import com.developlife.reviewtwits.repository.ReviewRepository;
import com.developlife.reviewtwits.service.user.UserService;
import com.developlife.reviewtwits.user.UserDocument;
import com.developlife.reviewtwits.user.UserSteps;
import io.restassured.builder.MultiPartSpecBuilder;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.developlife.reviewtwits.review.SnsReviewSteps.productURL;
import static com.developlife.reviewtwits.review.SnsReviewSteps.rightReviewText;
import static com.developlife.reviewtwits.review.SnsReviewSteps.starScore;
import static com.developlife.reviewtwits.review.SnsReviewSteps.wrongReviewText;
import static com.developlife.reviewtwits.review.SnsReviewSteps.리뷰_이미지_파일정보_생성;
import static com.developlife.reviewtwits.review.SnsReviewSteps.리뷰_이미지아닌_파일정보_생성;
import static com.developlife.reviewtwits.review.SnsReviewSteps.*;
import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.verify;

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

//    @Autowired
//    private AmazonS3 s3Client;

    private RegisterUserRequest registerUserRequest;

    @BeforeEach
    void settings(){
        registerUserRequest = userSteps.회원가입정보_생성();
        userService.register(registerUserRequest, UserSteps.일반유저권한_생성());
    }


    @Test
    void SNS_리뷰_작성_성공() throws IOException {

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
                        "SNS리뷰작성", UserDocument.AccessTokenHeader, SnsReviewDocument.SnsReviewWriteRequestField))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", token)
                .multiPart("productURL", productURL)
                .multiPart(multipartText("content", rightReviewText))
                .multiPart("score", starScore)
                .multiPart(multipartText("productName",productName));


        List<MultiPartSpecification> multiPartSpecList = 리뷰_이미지_파일정보_생성();

        for(MultiPartSpecification multiPartSpecification : multiPartSpecList){
            request.multiPart(multiPartSpecification);
        }

        request.when()
                .post("/sns/reviews")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all();

        List<Review> registeredReview = reviewRepository.findReviewsByProductUrl(productURL);
        assertThat(registeredReview.size()).isEqualTo(1);
    //    verify(s3Client).putObject(Mockito.any(PutObjectRequest.class));
    }

    @Test
    void SNS_리뷰작성_별점미입력_400(){

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec).log().all()
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", token)
                .multiPart("productURL", productURL)
                .multiPart(multipartText("content", rightReviewText))
                .multiPart(multipartText("productName", productName))
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
                .multiPart(multipartText("content",wrongReviewText))
                .multiPart("score", starScore)
                .multiPart(multipartText("productName", productName))
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
                .multiPart(multipartText("content",rightReviewText))
                .multiPart("score", starScore)
                .multiPart(multipartText("productName", productName));

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

        given(this.spec).log().all()
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", token)
                .multiPart("productURL", productURL)
                .multiPart(multipartText("content",wrongReviewText))
                .multiPart("score", starScore)
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
    void SNS_리뷰_피드_200() throws IOException {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        for(int writeCount = 1; writeCount <= 3; writeCount++){
            SNS_리뷰_작성(token, "review count : " + writeCount);
        }

        int size = 2;
        ExtractableResponse<Response> firstResponse = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "SNS 리뷰 피드를 요청하는 API 입니다." +
                        "<br> X-AUTH-PATH 를 넣어주지 않으면, 로그인 처리가 되지 않아 리액션에서 isReacted 가 모두 false 로 나타나게 됩니다." +
                        "<br> 처음에는 reviewId 를 넣어주지 않은 상태로 요청을 보내면 됩니다. 이 때, 가장 최근의 리뷰부터 나타나게 됩니다." +
                        "<br> size 값은 몇 개의 review 를 받을지 나타내는 부분으로써, 필수 값입니다. 넣어주지 않으면, 400 Bad Request 를 마주할 수 있습니다." +
                        "<br> 무한 스크롤을 구현하기 위해, 이후의 review 요청에서는 리뷰 리스트의 마지막 부분의 reviewId 를 넘겨주어야 합니다." +
                        "<br> 이 때, 남은 리뷰의 갯수가 요청한 size 보다 작을 경우, 남은 review 를 모두 넘겨주게 됩니다. " +
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
            SNS_리뷰_작성(token, "review count : " + writeCount);
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
    void SNS_리뷰_댓글작성_200() throws IOException {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long recentReviewId = SNS_리뷰_작성(token, "write review for comment test");


        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "SNS 리뷰 댓글을 작성하는 API 입니다." +
                        "<br> X-AUTH-TOKEN 은 필수로 Header 에 입력되어야 하며, 입력되지 않았을 경우 401 Unauthorized 가 나타나게 됩니다." +
                        "<br> 리뷰 아이디 또한 필수 값이며, Path 에 함께 입력할 수 있습니다." +
                        "<br> 리뷰 내용도 필수 값입니다." +
                        "<br> parentId 는 필수 값이며, 없을 경우 0을 입력해 주셔야 합니다. 음수를 입력할 수는 없습니다." +
                        "<br> parentId 가 0인 경우, 부모 댓글로 간주받게 되며, parentId 가 0이 아닐 경우, 부모 댓글의 대댓글로 간주됩니다." +
                        "<br> token 을 제외한 위의 규칙이 지켜지지 않을 경우, 400 Bad Request 가 반환됩니다." +
                        "<br> 모든 조건이 지켜진 상태라면, 댓글 저장과 함께 200 OK 가 반환됩니다.","SNS댓글작성",
                        UserDocument.AccessTokenHeader, SnsReviewDocument.ReviewIdField, SnsReviewDocument.SnsCommentWriteRequestField))
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
    void SNS_리뷰_댓글확인_200() throws IOException {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = SNS_리뷰_작성(token, "write review for comment test");
        SNS_리뷰_댓글_작성(token,registeredReviewId);

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "SNS 리뷰의 댓글을 확인하는 API 입니다." +
                        "리뷰 아이디가 path에 들어가며, 해당 아이디로 된 리뷰가 존재하지 않을 경우 404 Not Found 가 반환됩니다." +
                        "리뷰가 존재한다면, 200 OK 와 함께 리뷰의 댓글 리스트들이 반환됩니다.","SNS리뷰댓글확인"
                        ,SnsReviewDocument.ReviewIdField,SnsReviewDocument.SnsReviewCommentResponseField))
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
    void SNS_리뷰_댓글_삭제_성공_200() throws IOException {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = SNS_리뷰_작성(token, "write review for comment test");
        Long commentId = SNS_리뷰_댓글_작성(token, registeredReviewId);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "SNS 리뷰 댓글을 삭제하는 API 입니다." +
                        "<br>X-AUTH-TOKEN 은 필수값으로 header 에 들어가야 합니다." +
                        "<br>commentId 또한 필수 값이며, path 에 같이 입력할 수 있습니다." +
                        "<br>해당 유저가 댓글을 삭제할 권한이 없다면, 401 Unauthorized 가 반환됩니다." +
                        "<br>지우고자 하는 댓글이 존재하지 않으면, 404 Not Found 가 반환됩니다." +
                        "<br>정상적으로 삭제되었다면, 200 OK 가 반환됩니다.", "SNS리뷰댓글삭제"
                        ,UserDocument.AccessTokenHeader, SnsReviewDocument.CommentIdField))
                .header("X-AUTH-TOKEN",token)
                .pathParam("commentId", commentId)
                .when()
                .delete("/sns/comments/{commentId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all();

        List<Comment> commentList = commentRepository.findByReview_ReviewId(registeredReviewId);
        assertThat(commentList).isEmpty();
    }

    @Test
    void SNS_리뷰_댓글_수정_성공_200() throws IOException {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = SNS_리뷰_작성(token, "write review for comment test");
        Long commentId = SNS_리뷰_댓글_작성(token, registeredReviewId);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "SNS 리뷰 댓글을 수정하는 API 입니다." +
                        "<br>X-AUTH-TOKEN 은 필수값으로 header 에 들어가야 합니다." +
                        "<br>commentId 또한 필수 값이며, path 에 같이 입력할 수 있습니다." +
                        "<br>수정하고자 하는 내용은 필수 값이며, Http Request Query 에 작성할 수 있습니다." +
                        "<br>X-AUTH-HEADER 가 존재하지 않거나, 올바르지 않거나, 해당 유저가 댓글을 수정할 권한이 없다면 401 Unauthorized 가 반환됩니다." +
                        "<br>수정할 commentId 가 존재하지 않으면, 404 Not Found 가 반환됩니다." +
                        "<br>댓글 내용이 존재하지 않으면, 400 Bad Request 가 반환됩니다." +
                        "<br>댓글이 성공적으로 수정되었다면 200 OK 가 반환됩니다","SNS리뷰댓글수정",
                        UserDocument.AccessTokenHeader,SnsReviewDocument.CommentIdField, SnsReviewDocument.SnsCommentChangeRequestField))
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
    void 피드_리액션_추가_성공_200() throws IOException {

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = SNS_리뷰_작성(token, "write review for comment test");

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "피드 리액션 추가를 위한 API 입니다." +
                        "<br>X-AUTH-TOKEN 은 필수 값으로 header 에 들어가야 합니다." +
                        "<br>리액션을 추가할 reviewId 는 필수값이며, path 에 같이 추가될 수 있습니다." +
                        "<br>리액션 내용은 Request Body 에 넣을 수 있습니다." +
                        "<br>리액션 내용으로 넣을 수 있는 요소는 아래와 같습니다." +
                        "<br>LOVE,SUNGLASSES,LAUGHING,SURPRISING,THINKING," +
                                "<br>PLEADING,SHOCKING,PRAYING,GOOD,NOTICING",
                        "SNS리뷰리액션추가"
                        ,UserDocument.AccessTokenHeader,SnsReviewDocument.ReviewIdField,SnsReviewDocument.SnsReactionAddRequestField))
                .header("X-AUTH-TOKEN", token)
                .pathParam("reviewId", registeredReviewId)
                .param("reaction",reactionContent)
                .when()
                .post("/sns/review-reaction/{reviewId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all();

        JsonPath jsonPath = 피드_리뷰_리액션_추출(token);
        boolean isReacted = jsonPath.getBoolean("[0].reactionResponses."+ reactionContent + ".isReacted");

        assertThat(isReacted).isEqualTo(true);
    }

    @Test
    void 피드_리액션_삭제_성공_200() throws IOException {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        Long registeredReviewId = SNS_리뷰_작성(token, "write review for comment test");
        SNS_리액션_추가(token, registeredReviewId);

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "SNS 리뷰에 생성한 리액션을 취소하는 API 입니다." +
                        "<br>X-AUTH-TOKEN 은 필수 값으로 header 에 들어가야 합니다." +
                        "<br>리액션을 추가할 reviewId 는 필수값이며, path 에 같이 추가될 수 있습니다." +
                        "<br>X-AUTH_TOKEN 이 존재하지 않거나, 올바르지 않은 값이거나, 해당 리액션을 수정할 권한이 없는 경우 401 Unauthorized 가 반환됩니다." +
                        "<br>reviewId 가 존재하지 않은 경우, 404 Not Found 가 반환됩니다." +
                        "<br>올바른 값이 모두 들어갔다면, 리액션이 삭제되고 200 OK 가 반환됩니다."
                        ,"SNS피드리액션삭제",UserDocument.AccessTokenHeader,SnsReviewDocument.ReviewIdField))
                .header("X-AUTH-TOKEN",token)
                .pathParam("reviewId",registeredReviewId)
                .when()
                .delete("/sns/review-reaction/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all();

        JsonPath jsonPath = 피드_리뷰_리액션_추출(token);
        assertThat(jsonPath.getMap("[0].reactionResponses")).isEmpty();
    }

    Long SNS_리뷰_작성(String token, String content) throws IOException {

        RequestSpecification request = given(this.spec).log().all()
                .header("X-AUTH-TOKEN", token)
                .multiPart("productURL", productURL)
                .multiPart(multipartText("content", content))
                .multiPart("score", starScore)
                .multiPart(multipartText("productName",productName));

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

    private MultiPartSpecification multipartText(String controlName, String textBody) {
        return new MultiPartSpecBuilder(textBody)
                .controlName(controlName)
                .mimeType(String.valueOf(ContentType.TEXT))
                .charset(StandardCharsets.UTF_8)
                .build();
    }

    Long SNS_리뷰_댓글_작성(String token, long reviewId){

        given(this.spec)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN",token)
                .pathParam("reviewId", reviewId)
                .body(SnsReviewSteps.댓글_작성정보_생성())
                .when()
                .post("/sns/comments/{reviewId}")
                .then()
                .log().all();

        List<Comment> recentComments = commentRepository.findByReview_ReviewId(reviewId);
        return recentComments.get(recentComments.size()-1).getCommentId();
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
}