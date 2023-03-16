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
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
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

        final String token = userSteps.로그인토큰정보(UserSteps.로그인요청생성()).accessToken();

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
    void 쇼핑몰리뷰작성_별점미입력_400(){

        final String token = userSteps.로그인토큰정보(UserSteps.로그인요청생성()).accessToken();

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
        final String token = userSteps.로그인토큰정보(UserSteps.로그인요청생성()).accessToken();

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

        final String token = userSteps.로그인토큰정보(UserSteps.로그인요청생성()).accessToken();

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
        final String token = userSteps.로그인토큰정보(UserSteps.로그인요청생성()).accessToken();

        final String wrongToken = "wrongToken";

        given(this.spec).log().all()
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN", wrongToken)
                .multiPart("productURL", productURL)
                .multiPart("content",rightReviewText)
                .multiPart("score", starScore)
                .when()
                .get("/reviews/shopping")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("find{it.errorType == 'TokenInvalidException' }", notNullValue())
                .log().all().extract();
    }*/
}
