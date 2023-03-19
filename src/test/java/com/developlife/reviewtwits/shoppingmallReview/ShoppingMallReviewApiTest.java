package com.developlife.reviewtwits.shoppingmallReview;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.CommonDocument;
import com.developlife.reviewtwits.entity.Product;
import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.mapper.ProjectMapper;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.repository.ProductRepository;
import com.developlife.reviewtwits.repository.ProjectRepository;
import com.developlife.reviewtwits.service.user.UserService;
import com.developlife.reviewtwits.user.UserDocument;
import com.developlife.reviewtwits.user.UserSteps;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.util.List;

import static com.developlife.reviewtwits.shoppingmallReview.ShoppingMallReviewSteps.*;
import static io.restassured.RestAssured.*;
import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
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

    private Product product;
    private Project project;
    private RegisterUserRequest registerUserRequest;

    @BeforeEach
    void settings(){
        registerUserRequest = userSteps.회원가입정보_생성();
        userService.register(registerUserRequest, UserSteps.일반유저권한_생성());

        project = 임시_프로젝트정보_생성(projectMapper, projectRepository);
        product = 임시_상품정보_생성(project, productRepository);
    }

    @Test
    void 쇼핑몰리뷰작성_성공_200() throws IOException {

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        RequestSpecification request = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,"쇼핑몰 리뷰를 작성합니다. 필수값이 입력되지 않았을 경우 400이 반환됩니다.","쇼핑몰리뷰작성", UserDocument.AccessTokenHeader ,ShoppingMallReviewDocument.ShoppingMallReviewWriteRequestField))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", token)
                .multiPart("productURL", productURL)
                .multiPart("content",rightReviewText)
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
                .statusCode(HttpStatus.OK.value())
                .log().all();
    }

    @Test
    void 쇼핑몰리뷰정보_제품별찾기_200() throws IOException{
        // 먼저 쇼핑몰리뷰를 보내기

        쇼핑몰_리뷰_등록();

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "쇼핑몰 제품의 리뷰 전체 요약 정보를 반환합니다. product URL 이 입력되지 않았을 경우 400 이 반환됩니다.","쇼핑몰 리뷰 요약정보",ShoppingMallReviewDocument.ReviewProductRequestField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(ShoppingMallReviewSteps.제품_URL_정보_생성())
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
    void 쇼핑몰리뷰정보_리뷰리스트_반환_200() throws IOException {
        쇼핑몰_리뷰_등록();

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "원하는 제품의 쇼핑몰 리뷰 리스트를 반환합니다. product URL 이 입력되지 않았을 경우 400 이 반환됩니다.", "쇼핑몰 리뷰 리스트",ShoppingMallReviewDocument.ReviewProductRequestField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(ShoppingMallReviewSteps.제품_URL_정보_생성())
                .when()
                .get("/reviews/shopping/list")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$", Matchers.notNullValue())
                .log().all();
    }

    @Test
    void 쇼핑몰_리뷰_삭제_200() throws IOException {
        쇼핑몰_리뷰_등록();
        long reviewId = 리뷰아이디_추출();

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,"입력한 리뷰 아이디의 리뷰를 삭제 처리합니다." +
                        "<br>입력된 아이디로 등록된 리뷰가 없는 경우 404 Not Found 가 리턴됩니다." +
                        "<br>해당 리뷰를 수정할 권한이 없는 경우, 401 Unauthorized 가 리턴됩니다","쇼핑몰리뷰삭제", ShoppingMallReviewDocument.ReviewIdField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .pathParam("reviewId",reviewId)
                .when()
                .delete("/reviews/shopping/{reviewId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all();

        JsonPath jsonPath = JsonPath_추출();
        assertThat(jsonPath.getBoolean("[0].exist")).isFalse();
    }

    private JsonPath JsonPath_추출() {
        ExtractableResponse<Response> response = given(this.spec)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(제품_URL_정보_생성())
                .when()
                .get("/reviews/shopping/list")
                .then()
                .log().all().extract();

        return response.jsonPath();
    }

    private long 리뷰아이디_추출(){

        JsonPath jsonPath = JsonPath_추출();
        return jsonPath.getLong("[0].reviewId");
    }

    private void 쇼핑몰_리뷰_등록() throws IOException {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        RequestSpecification request = given(this.spec).log().all()
                .filter(document(DEFAULT_RESTDOC_PATH,"쇼핑몰 리뷰를 작성합니다. 필수값이 입력되지 않았을 경우 400이 반환됩니다.","쇼핑몰리뷰작성", UserDocument.AccessTokenHeader ,ShoppingMallReviewDocument.ShoppingMallReviewWriteRequestField))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", token)
                .multiPart("productURL", productURL)
                .multiPart("content",rightReviewText)
                .multiPart("score", starScore);

        List<MultiPartSpecification> multiPartSpecList = 리뷰_이미지_파일정보_생성();

        for(MultiPartSpecification multiPartSpecification : multiPartSpecList){
            request.multiPart(multiPartSpecification);
        }

        request
                .when()
                .post("/reviews/shopping")
                .then().log().all();
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
    }
/*
    @Test
    void 쇼핑몰리뷰작성_유효하지않은토큰_401(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        final String wrongToken = "wrongToken";

        given(this.spec).log().all()
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
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
                .body("find{it.errorType == 'TokenInvalidException' }", notNullValue())
                .log().all().extract();
    }*/
}
