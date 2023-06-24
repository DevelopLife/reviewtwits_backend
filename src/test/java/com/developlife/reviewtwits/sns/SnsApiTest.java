package com.developlife.reviewtwits.sns;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.CommonDocument;
import com.developlife.reviewtwits.entity.Follow;
import com.developlife.reviewtwits.entity.Product;
import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.sns.FollowRequest;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.project.ProjectSteps;
import com.developlife.reviewtwits.repository.ProductRepository;
import com.developlife.reviewtwits.repository.ProjectRepository;
import com.developlife.reviewtwits.repository.follow.FollowRepository;
import com.developlife.reviewtwits.review.ShoppingMallReviewSteps;
import com.developlife.reviewtwits.review.SnsReviewSteps;
import com.developlife.reviewtwits.service.ProjectService;
import com.developlife.reviewtwits.service.user.UserService;
import com.developlife.reviewtwits.user.UserDocument;
import com.developlife.reviewtwits.user.UserSteps;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Optional;

import static com.developlife.reviewtwits.review.ShoppingMallReviewSteps.임시_상품정보_생성;
import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;

/**
 * @author WhalesBob
 * @since 2023-03-20
 */
public class SnsApiTest extends ApiTest {

    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserSteps userSteps;
    @Autowired
    private SnsSteps snsSteps;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SnsReviewSteps snsReviewSteps;

    private RegisterUserRequest registerUserRequest;
    private User user;
    private User targetUser;
    private Project project;
    private Product product;

    @BeforeEach
    void settings() throws IOException {
        // 당연히 로그인이 되어 있는 상태여야 한다.
        registerUserRequest = userSteps.회원가입정보_생성();
        user = userService.register(registerUserRequest, UserSteps.일반유저권한_생성());
        final String userToken = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        추가회원가입정보_입력(userToken, SnsSteps.userNickname);

        registerUserRequest = userSteps.상대유저_회원가입정보_생성();
        targetUser = userService.register(registerUserRequest, UserSteps.일반유저권한_생성());
        final String targetUserToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());
        추가회원가입정보_입력(targetUserToken, SnsSteps.targetUserNickname);

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        projectService.registerProject(ProjectSteps.프로젝트생성요청_생성(0), user);
        project = projectRepository.findAll().get(0);

        product = 임시_상품정보_생성(project, productRepository);

        snsReviewSteps.아이템정보생성();
        Long reviewId = snsReviewSteps.SNS_리뷰_작성(token, "페로로쉐 초콜릿 좋아요");
        snsReviewSteps.SNS_리뷰_댓글_작성(token, reviewId);
        snsReviewSteps.SNS_리액션_추가(token,reviewId);
    }

    @Test
    void 팔로우요청_기본_성공_200(){

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "팔로우 요청을 보냅니다." +
                        "<br>팔로우가 정상적으로 이루어졌다면 200 OK 가 반환됩니다." +
                        "<br>입력된 닉네임으로 등록된 계정이 없는 경우 404 Not Found 가 반환됩니다." +
                        "<br>이미 요청되어 성사되어 있는 팔로우를 다시 요청하면 409 Conflict 가 반환됩니다" +
                        "<br>유효한 access token 이 아닐 경우, 403 Forbidden 이 반환됩니다.", "팔로우요청",
                        UserDocument.AccessTokenHeader,SnsDocument.followRequestField, SnsDocument.followResultResponseField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(snsSteps.팔로우정보_생성())
                .when()
                .post("/sns/request-follow")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();

        assertThat(jsonPath.getString("targetUserInfoResponse.nickname")).isEqualTo(SnsSteps.targetUserNickname);
    }

    @Test
    void 팔로우요청_맞팔로우_성공_200(){

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        팔로우요청_생성(token, snsSteps.팔로우정보_생성());

        final String oppositeToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,UserDocument.AccessTokenHeader,SnsDocument.followRequestField,SnsDocument.followResultResponseField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", oppositeToken)
                .body(snsSteps.팔로우정보_상대방측_생성())
                .when()
                .post("/sns/request-follow")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();

        Optional<Follow> newFollow = followRepository.findByUserAndTargetUser(user, targetUser);
        assertThat(newFollow.isPresent()).isTrue();

        assertThat(jsonPath.getBoolean("followBackFlag")).isTrue();
    }


    private void 팔로우요청_생성(String token, FollowRequest request) {
        given(this.spec)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(request)
                .when()
                .post("/sns/request-follow")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }
    @Test
    void 팔로우요청_헤더없이요청_401(){

        given(this.spec)
                    .filter(document(DEFAULT_RESTDOC_PATH))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(snsSteps.팔로우정보_생성())
                    .when()
                    .post("/sns/request-follow")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .log().all().extract();
    }

    @Test
    void 팔로우요청_없는계정팔로우요청_404(){

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(snsSteps.없는상대방_팔로우요청_생성())
                .when()
                .post("/sns/request-follow")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("find{it.errorType == 'UserIdNotFoundException' " +
                        "&& it.fieldName == 'userId' }", notNullValue())
                .log().all().extract();;
    }

    @Test
    void 팔로우요청_이미존재하는팔로우_409(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        팔로우요청_생성(token, snsSteps.팔로우정보_생성());

        final String tokenAgain = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", tokenAgain)
                .body(snsSteps.팔로우정보_생성())
                .when()
                .post("/sns/request-follow")
                .then()
                .assertThat()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("find{it.errorType == 'FollowAlreadyExistsException' " +
                        "&& it.fieldName == 'targetUserAccountId' }", notNullValue())
                .log().all().extract();

    }

    @Test
    void 언팔로우요청_기본_성공_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        팔로우요청_생성(token, snsSteps.팔로우정보_생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,"언팔로우 요청을 보냅니다." +
                        "<br>언팔로우가 정상적으로 동작했다면 200 OK 가 반환됩니다." +
                        "<br>입력된 아이디로 등록된 계정이 없는 경우 404 Not Found 가 반환됩니다." +
                        "<br>이미 팔로우되어 있지 않은 계정에 언팔로우를 요청하면 409 Conflict 가 반환됩니다" +
                        "<br>유효한 access token 이 아닐 경우, 403 Forbidden 이 반환됩니다.","언팔로우요청",
                        UserDocument.AccessTokenHeader,SnsDocument.followRequestField,SnsDocument.followResultResponseField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(snsSteps.팔로우정보_생성())
                .when()
                .post("/sns/request-unfollow")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());

        Optional<Follow> newFollow = followRepository.findByUserAndTargetUser(user, targetUser);
        assertThat(newFollow.isPresent()).isFalse();
    }

    @Test
    void 언팔로우요청_맞팔로우해제_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        팔로우요청_생성(token, snsSteps.팔로우정보_생성());

        final String oppositeToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());
        팔로우요청_생성(oppositeToken, snsSteps.팔로우정보_상대방측_생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        UserDocument.AccessTokenHeader,SnsDocument.followRequestField,SnsDocument.followResultResponseField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(snsSteps.팔로우정보_생성())
                .when()
                .post("/sns/request-unfollow")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all();

        Optional<Follow> follow = followRepository.findByUserAndTargetUser(user, targetUser);
        Optional<Follow> backFollow = followRepository.findByUserAndTargetUser(targetUser, user);

        assertThat(follow.isPresent()).isFalse();
        assertThat(backFollow.isPresent()).isTrue();
        assertThat(backFollow.get().isFollowBackFlag()).isFalse();
    }

    @Test
    void 언팔로우요청_헤더없이요청_401(){
        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(snsSteps.팔로우정보_생성())
                .when()
                .post("/sns/request-unfollow")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all().extract();
    }

    @Test
    void 언팔로우요청_없는계정언팔로우요청_404(){

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(snsSteps.없는상대방_팔로우요청_생성())
                .when()
                .post("/sns/request-unfollow")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("find{it.errorType == 'UserIdNotFoundException' " +
                        "&& it.fieldName == 'userId' }", notNullValue())
                .log().all().extract();
    }

    @Test
    void 언팔로우요청_이미_맞팔로우_아님_409(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(snsSteps.팔로우정보_생성())
                .when()
                .post("/sns/request-unfollow")
                .then()
                .assertThat()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("find{it.errorType == 'UnfollowAlreadyDoneException' " +
                        "&& it.fieldName == 'targetUserAccountId' }", notNullValue())
                .log().all().extract();
    }

    @Test
    void 팔로워리스트_요청_성공_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        팔로우요청_생성(token, snsSteps.팔로우정보_생성());

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "팔로워 리스트를 요청합니다." +
                        "<br> 정상적인 이메일 형식의 아이디를 입력해 성공하면, 200 OK 코드와 함께 유저 정보 리스트가 반환됩니다." +
                        "<br>추가적으로, 페이징 작업을 위해서 userId 와 size 를 입력해야 합니다." +
                        "<br>받은 팔로워 리스트 맨 아래에 있는 userId 를 입력하면, 그 다음 부분의 팔로워 리스트를 보여 줍니다." +
                        "<br>userId 를 입력하지 않으면, 가장 최근에 이루어진 팔로잉 순으로 팔로워 리스트가 반환됩니다. " +
                        "<br>size 는 필수 입력값입니다. 1 이상으로 입력해야 하며, 조건이 맞지 않을 시 400 Bad Request 가 반환됩니다." +
                        "<br> 가입되어 있지 않은 아이디가 입력되면, 404 Not Found 와 함께 오류 메세지가 반환됩니다.",
                        "팔로워리스트요청",SnsDocument.userNicknameField,
                        SnsDocument.userIdAndPageSizeRequestField,
                        SnsDocument.userListResponseField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("nickname", SnsSteps.targetUserNickname)
                .param("size", SnsSteps.followRequestSize)
                .when()
                .get("/sns/get-followers/{nickname}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getString("[0].nickname")).isEqualTo(SnsSteps.userNickname);
    }

    @Test
    void 팔로워리스트_요청_없는아이디_404(){

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("nickname", SnsSteps.notExistNickname)
                .param("size", SnsSteps.followRequestSize)
                .when()
                .get("/sns/get-followers/{nickname}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("find{it.errorType == 'UserIdNotFoundException' " +
                        "&& it.fieldName == 'userId' }", notNullValue())
                .log().all();
    }
    @Test
    void 팔로워리스트_요청_사이즈값이상_400(){

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("nickname", SnsSteps.targetUserNickname)
                .param("size", -1)
                .when()
                .get("/sns/get-followers/{nickname}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all();
    }

    @Test
    void 팔로잉리스트_요청_성공_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        팔로우요청_생성(token, snsSteps.팔로우정보_생성());

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "팔로잉 리스트를 요청합니다." +
                        "<br> 정상적인 이메일 형식의 아이디를 입력해 성공하면, 200 OK 코드와 함께 유저 정보 리스트가 반환됩니다." +
                        "<br>추가적으로, 페이징 작업을 위해서 userId 와 size 를 입력해야 합니다." +
                        "<br>받은 팔로우 리스트 맨 아래에 있는 userId 를 입력하면, 그 다음 부분의 팔로잉 리스트를 보여 줍니다." +
                        "<br>userId 를 입력하지 않으면, 가장 최근에 이루어진 팔로잉 순으로 팔로잉 리스트가 반환됩니다. " +
                        "<br>size 는 필수 입력값입니다. 1 이상으로 입력해야 하며, 조건이 맞지 않을 시 400 Bad Request 가 반환됩니다." +
                        "<br>가입되어 있지 않은 아이디가 입력되면, 404 Not Found 와 함께 오류 메세지가 반환됩니다."
                        ,"팔로잉리스트요청",SnsDocument.userNicknameField,
                        SnsDocument.userIdAndPageSizeRequestField,
                        SnsDocument.userListResponseField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("nickname", SnsSteps.userNickname)
                .param("size",SnsSteps.followRequestSize)
                .when()
                .get("/sns/get-followings/{nickname}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getString("[0].nickname")).isEqualTo(SnsSteps.targetUserNickname);
    }

    @Test
    void 팔로잉리스트_요청_없는아이디_404(){

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("nickname", SnsSteps.notExistNickname)
                .param("size",SnsSteps.followRequestSize)
                .when()
                .get("/sns/get-followings/{nickname}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("find{it.errorType == 'UserIdNotFoundException' " +
                        "&& it.fieldName == 'userId' }", notNullValue())
                .log().all();
    }

    @Test
    @DisplayName("SNS 검색")
    void SNS검색_아이템과리뷰_200() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
            .filter(document(DEFAULT_RESTDOC_PATH, "리뷰와 상품에 대한 정보를 검색합니다(조회 가능 글자 범위는 2-20)",
                "SNS 전체검색 기능",
                SnsDocument.SearchAllSnsRequest,
                SnsDocument.SearchAllSnsResponse
            ))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("X-AUTH-TOKEN", token)
            .param("searchKey", "페로로쉐")
        .when()
            .get("/sns/search")
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .log().all();
    }

    @Test
    @DisplayName("SNS 검색")
    void SNS_상품추천_200() {

        given(this.spec)
            .filter(document(DEFAULT_RESTDOC_PATH, "최대 3개의 상품을 추천해줍니다",
                "SNS 상품추천",
                SnsDocument.RecommendProductResponse
            ))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/sns/recommend-product")
            .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .log().all();
    }


    @Test
    @DisplayName("팔로워 추천")
    void SNS_팔로워추천_200() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
            .filter(document(DEFAULT_RESTDOC_PATH,
                "최대 5명의 팔로우를 추천해줍니다(페이징이 필요하면 말해주세요) 임시로 최근 만들어진 계정으로 반환합니다",
                "SNS 팔로워추천",
                UserDocument.AccessTokenHeader,
                SnsDocument.FollowerRecommendResponse
            ))
            .header("X-AUTH-TOKEN", token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/sns/suggest-followers")
            .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .log().all();
    }

    @Test
    void SNS_개인페이지_프로필정보_요청_200() {

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        팔로우요청_생성(token, snsSteps.팔로우정보_생성());

        final String oppositeToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());
        팔로우요청_생성(oppositeToken, snsSteps.팔로우정보_상대방측_생성());

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "SNS 개인 프로필을 요청하는 API 입니다." +
                        "<br>존재하는 유저의 닉네임으로 조회 시 200 OK 와 함께 유저 프로필 정보를 반환합니다." +
                        "<br>이때 header 에 X-AUTH-TOKEN 이 입력되어 있을 시 프로필 이미지를 보고자 하는 유저가 " +
                        "<br>목표하는 유저를 팔로우하고 있는지 여부도 같이 돌려받을 수 있습니다." +
                        "<br>header 정보가 없으면, 로그인되어 있지 않다고 간주하고 false 를 반환합니다." +
                        "<br>입력한 닉네임의 가입정보가 존재하지 않는 경우 404 Not Found 가 반한됩니다.",
                        "개인프로필정보요청",UserDocument.OptionalAccessTokenHeader,
                        SnsDocument.userNicknameField,
                        SnsDocument.UserProfileInfoResponse ))
                .header("X-AUTH-TOKEN", oppositeToken)
                .pathParam("nickname", SnsSteps.userNickname)
                .when()
                .get("/sns/profile/{nickname}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getString("nickname")).isEqualTo(SnsSteps.userNickname);
        assertThat(jsonPath.getInt("reviewCount")).isEqualTo(1);
        assertThat(jsonPath.getInt("followers")).isEqualTo(1);
        assertThat(jsonPath.getInt("followings")).isEqualTo(1);
        assertThat(jsonPath.getBoolean("isFollowed")).isTrue();
    }

    @Test
    void 개인페이지_프로필정보_요청_헤더없음_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        팔로우요청_생성(token, snsSteps.팔로우정보_생성());

        final String oppositeToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());
        팔로우요청_생성(oppositeToken, snsSteps.팔로우정보_상대방측_생성());

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,UserDocument.OptionalAccessTokenHeader,
                        SnsDocument.userNicknameField,
                        SnsDocument.UserProfileInfoResponse ))
                .pathParam("nickname", SnsSteps.userNickname)
                .when()
                .get("/sns/profile/{nickname}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getString("nickname")).isEqualTo(SnsSteps.userNickname);
        assertThat(jsonPath.getInt("reviewCount")).isEqualTo(1);
        assertThat(jsonPath.getInt("followers")).isEqualTo(1);
        assertThat(jsonPath.getInt("followings")).isEqualTo(1);
        assertThat(jsonPath.getBoolean("isFollowed")).isFalse();
    }

    @Test
    void SNS_개인페이지_프로필정보_없는계정요청_404(){
        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .pathParam("nickname","notRegistered")
                .when()
                .get("/sns/profile/{nickname}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all();
    }

    @Test
    void SNS_개인페이지_리뷰리스트_요청_성공_200() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        snsReviewSteps.SNS_리뷰_작성(token,"2번째 리뷰를 작성합니다.");
        snsReviewSteps.SNS_리뷰_작성(token,"3번째 리뷰를 작성합니다.");

        int size = 2;

        ExtractableResponse<Response> firstResponse = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "개인 페이지에서의 리뷰 리스트를 요청하는 API 입니다." +
                        "<br>알고 싶은 계정의 닉네임을 path 에 입력하면, 해당 계정이 작성한 리뷰들의 간략한 정보를 알 수 있습니다." +
                        "<br>존재하지 않는 닉네임을 입력하면 404 Not Found 가 반환됩니다." +
                        "<br>size 는 필수값입니다. 원하는 숫자 단위로 review 정보를 받을 수 있습니다." +
                        "<br>받은 리뷰들 이전에 작성된 리뷰들을 받고 싶다면, reviewId 를 입력해 주셔야 합니다." +
                        "<br>예를 들어, review 2개를 요청해 10번, 9번 reviewId 까지 받았다면, ?reviewId=9 를 입력하는 방식입니다." +
                        "<br>size 와 reviewId 는 Query String 으로 입력해 주셔야 합니다. size 가 없다면, 400 이 리턴됩니다.",
                        "개인리뷰리스트요청",
                        SnsDocument.userNicknameField,SnsDocument.ReviewIdAndSizeField
                        /*SnsDocument.UserSnsReviewResponse*/))
                .pathParam("nickname", SnsSteps.userNickname)
                .param("size",size)
                .when()
                .get("/sns/profile/reviews/{nickname}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = firstResponse.jsonPath();

        assertThat(jsonPath.getList("").size()).isEqualTo(size);
        assertThat(jsonPath.getInt("[0].commentCount")).isEqualTo(0);
        assertThat(jsonPath.getInt("[0].reactionCount")).isEqualTo(0);
        assertThat(jsonPath.getString("[0].reviewImageUrlList")).isNotEmpty();

        Long reviewId = jsonPath.getLong("[1].reviewId");

        ExtractableResponse<Response> secondResponse = given()
                .pathParam("nickname", SnsSteps.userNickname)
                .param("size", size)
                .param("reviewId", reviewId)
                .when()
                .get("/sns/profile/reviews/{nickname}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath secondPath = secondResponse.jsonPath();

        assertThat(secondPath.getList("").size()).isEqualTo(1);
        assertThat(secondPath.getInt("[0].commentCount")).isEqualTo(1);
        assertThat(secondPath.getInt("[0].reactionCount")).isEqualTo(1);
    }

    @Test
    void SNS_개인페이지_리뷰리스트_존재하지않는계정_404() {

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .pathParam("nickname", "notRegistered")
                .when()
                .param("size",2)
                .get("/sns/profile/reviews/{nickname}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all();
    }

    @Test
    void 최근리뷰쓴유저_요청_성공_200(){

        final String oppositeToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());
        팔로우요청_생성(oppositeToken,snsSteps.팔로우정보_상대방측_생성());

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "최근에 리뷰를 작성한 사람을 size 크기 이내로 돌려 주는 API 입니다." +
                                "<br>size 값을 넣어주지 않는다면, 5명을 default 값으로 동작하게 됩니다." +
                                "<br>유저 토큰이 없거나, 팔로우한 사람이 없으면 빈 리스트를 돌려받게 됩니다. " +
                                "<br>size 값은 1 이상의 정수로 입력해야 하며, 잘못된 값이 입력되었을 경우 400 Bad Request 를 돌려받습니다.",
                        "최근리뷰쓴유저요청",
                        UserDocument.OptionalAccessTokenHeader,
                        SnsDocument.ReviewSizeField,
                        SnsDocument.userListResponseField))
                .header("X-AUTH-TOKEN", oppositeToken)
                .param("size", 5)
                .when()
                .get("/sns/recent-update-users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getList("").size()).isEqualTo(1);
        assertThat(jsonPath.getString("[0].nickname")).isEqualTo(SnsSteps.userNickname);
    }

    @Test
    void 최근리뷰쓴유저_요청_토큰없음_빈리스트_200(){
        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                                UserDocument.OptionalAccessTokenHeader,
                                SnsDocument.ReviewSizeField))
                .param("size", 5)
                .when()
                .get("/sns/recent-update-users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getList("")).isEmpty();
    }

    @Test
    void 최근리뷰쓴유저_팔로잉없음_빈리스트_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        UserDocument.OptionalAccessTokenHeader,
                        SnsDocument.ReviewSizeField))
                .header("X-AUTH-TOKEN", token)
                .param("size", 5)
                .when()
                .get("/sns/recent-update-users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getList("")).isEmpty();
    }

    @Test
    void 최근리뷰쓴유저_사이즈값미입력_기본값5_성공_200(){
        final String oppositeToken = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());
        팔로우요청_생성(oppositeToken,snsSteps.팔로우정보_상대방측_생성());

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        UserDocument.OptionalAccessTokenHeader,
                        SnsDocument.ReviewSizeField,
                        SnsDocument.userListResponseField))
                .header("X-AUTH-TOKEN", oppositeToken)
                .when()
                .get("/sns/recent-update-users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getList("").size()).isEqualTo(1);
        assertThat(jsonPath.getString("[0].nickname")).isEqualTo(SnsSteps.userNickname);
    }

    @Test
    void 최근리뷰쓴유저_사이즈값이상_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.상대유저_로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,CommonDocument.ErrorResponseFields))
                .header("X-AUTH-TOKEN",token)
                .param("size",-1)
                .when()
                .get("/sns/recent-update-users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
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