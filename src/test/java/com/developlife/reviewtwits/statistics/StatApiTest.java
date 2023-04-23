package com.developlife.reviewtwits.statistics;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.entity.Product;
import com.developlife.reviewtwits.entity.Project;
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
import io.restassured.specification.MultiPartSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

import static com.developlife.reviewtwits.review.ShoppingMallReviewSteps.임시_상품정보_생성;
import static com.developlife.reviewtwits.review.ShoppingMallReviewSteps.임시_프로젝트정보_생성;
import static io.restassured.RestAssured.given;

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

        given(this.spec)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN",token)
                .body(StatInfoSteps.통계정보_생성())
                .when()
                .post("/statistics/visited-info")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

    }

    @Test
    void 통계정보_등록_유저정보없음_성공_200(){

        given(this.spec)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(StatInfoSteps.통계정보_생성())
                .when()
                .post("/statistics/visited-info")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
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