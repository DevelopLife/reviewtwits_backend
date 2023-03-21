package com.developlife.reviewtwits.sns;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.CommonDocument;
import com.developlife.reviewtwits.entity.Follow;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.sns.FollowRequest;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.repository.FollowRepository;
import com.developlife.reviewtwits.service.user.UserService;
import com.developlife.reviewtwits.user.UserSteps;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Optional;

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
    private UserSteps userSteps;
    @Autowired
    private SnsSteps snsSteps;
    @Autowired
    private FollowRepository followRepository;

    private RegisterUserRequest registerUserRequest;
    private User user;
    private User targetUser;

    @BeforeEach
    void settings(){
        // 당연히 로그인이 되어 있는 상태여야 한다.
        registerUserRequest = userSteps.회원가입정보_생성();
        user = userService.register(registerUserRequest, UserSteps.일반유저권한_생성());

        registerUserRequest = userSteps.팔로우상대방_회원가입정보_생성();
        targetUser = userService.register(registerUserRequest, UserSteps.일반유저권한_생성());
    }

    @Test
    void 팔로우요청_기본_성공_200(){

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,"팔로우 요청을 보냅니다." +
                        "<br>팔로우가 정상적으로 이루어졌다면 200 OK 가 반환됩니다." +
                        "<br>입력된 아이디로 등록된 계정이 없는 경우 404 Not Found 가 반환됩니다." +
                        "<br>이미 요청되어 성사되어 있는 팔로우를 다시 요청하면 409 Conflict 가 반환됩니다" +
                        "<br>이메일 형식의 아이디가 아닌 경우, 400 Bad Request 가 반환됩니다." +
                        "<br>유효한 access token 이 아닐 경우, 401 Unauthorized 가 반환됩니다.","팔로우요청", SnsDocument.followRequestField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(snsSteps.팔로우정보_생성())
                .when()
                .post("/sns/request-follow")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());

        Optional<Follow> newFollow = followRepository.findByUserAndTargetUser(user, targetUser);
        assertThat(newFollow.isPresent()).isTrue();
    }

    @Test
    void 팔로우요청_맞팔로우_성공_200(){

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        팔로우요청_생성(token, snsSteps.팔로우정보_생성());

        final String oppositeToken = userSteps.로그인액세스토큰정보(UserSteps.팔로우상대_로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,SnsDocument.followRequestField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", oppositeToken)
                .body(snsSteps.팔로우정보_상대방측_생성())
                .when()
                .post("/sns/request-follow")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());

        Optional<Follow> newFollow = followRepository.findByUserAndTargetUser(user, targetUser);
        assertThat(newFollow.isPresent()).isTrue();

        Follow followInfo = newFollow.get();
        assertThat(followInfo.isFollowBackFlag()).isTrue();
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
    void 팔로우요청_이메일형식아님_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(snsSteps.이메일형식아닌_팔로우요청_생성())
                .when()
                .post("/sns/request-follow")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("find{it.errorType == 'Email' " +
                        "&& it.fieldName == 'targetUserAccountId' }", notNullValue())
                .log().all().extract();;
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
                        "<br>이메일 형식의 아이디가 아닌 경우, 400 Bad Request 가 반환됩니다." +
                        "<br>유효한 access token 이 아닐 경우, 401 Unauthorized 가 반환됩니다.","언팔로우요청", SnsDocument.followRequestField))
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

        final String oppositeToken = userSteps.로그인액세스토큰정보(UserSteps.팔로우상대_로그인요청생성());
        팔로우요청_생성(oppositeToken, snsSteps.팔로우정보_상대방측_생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, SnsDocument.followRequestField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(snsSteps.팔로우정보_생성())
                .when()
                .post("/sns/request-unfollow")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());

        Optional<Follow> follow = followRepository.findByUserAndTargetUser(user, targetUser);
        Optional<Follow> backFollow = followRepository.findByUserAndTargetUser(targetUser, user);

        assertThat(follow.isPresent()).isFalse();
        assertThat(backFollow.isPresent()).isTrue();
        assertThat(backFollow.get().isFollowBackFlag()).isFalse();
    }

    @Test
    void 팔로우요청_없는계정언팔로우요청_404(){

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
                .log().all().extract();;
    }

    @Test
    void 언팔로우요청_이메일형식아님_400(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN", token)
                .body(snsSteps.이메일형식아닌_팔로우요청_생성())
                .when()
                .post("/sns/request-unfollow")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("find{it.errorType == 'Email' " +
                        "&& it.fieldName == 'targetUserAccountId' }", notNullValue())
                .log().all().extract();;
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
                        "<br> 이메일 형식의 아이디가 아니라면, 400 BAD REQUEST 와 함께 오류 메세지가 반환됩니다." +
                        "<br> 가입되어 있지 않은 아이디가 입력되면, 404 Not Found 와 함께 오류 메세지가 반환됩니다.","팔로워리스트요청",SnsDocument.followIdField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("accountId", SnsSteps.targetUserAccountId)
                .when()
                .get("/sns/get-followers/{accountId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getString("[0].accountId")).isEqualTo(SnsSteps.userAccountId);
    }

    @Test
    void 팔로워리스트_요청_이메일형식아님_400(){
        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("accountId", "notEmail")
                .when()
                .get("/sns/get-followers/{accountId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("find{it.errorType == 'ConstraintViolationException' " +
                        "&& it.message == '올바른 형식의 이메일 주소여야 합니다' }", notNullValue())
                .log().all();
    }

    @Test
    void 팔로워리스트_요청_없는아이디_404(){

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("accountId", SnsSteps.notExistAccountId)
                .when()
                .get("/sns/get-followers/{accountId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("find{it.errorType == 'UserIdNotFoundException' " +
                        "&& it.fieldName == 'userId' }", notNullValue())
                .log().all();
    }

    @Test
    void 팔로잉리스트_요청_성공_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        팔로우요청_생성(token, snsSteps.팔로우정보_생성());

        ExtractableResponse<Response> response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "팔로잉 리스트를 요청합니다." +
                        "<br> 정상적인 이메일 형식의 아이디를 입력해 성공하면, 200 OK 코드와 함께 유저 정보 리스트가 반환됩니다." +
                        "<br> 이메일 형식의 아이디가 아니라면, 400 BAD REQUEST 와 함께 오류 메세지가 반환됩니다." +
                        "<br> 가입되어 있지 않은 아이디가 입력되면, 404 Not Found 와 함께 오류 메세지가 반환됩니다.","팔로잉리스트요청",SnsDocument.followIdField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("accountId", SnsSteps.userAccountId)
                .when()
                .get("/sns/get-followings/{accountId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all().extract();

        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getString("[0].accountId")).isEqualTo(SnsSteps.targetUserAccountId);
    }

    @Test
    void 팔로잉리스트_요청_이메일형식아님_400(){
        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("accountId", "notEmail")
                .when()
                .get("/sns/get-followers/{accountId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("find{it.errorType == 'ConstraintViolationException' " +
                        "&& it.message == '올바른 형식의 이메일 주소여야 합니다' }", notNullValue())
                .log().all();
    }

    @Test
    void 팔로잉리스트_요청_없는아이디_404(){

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("accountId", SnsSteps.notExistAccountId)
                .when()
                .get("/sns/get-followers/{accountId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("find{it.errorType == 'UserIdNotFoundException' " +
                        "&& it.fieldName == 'userId' }", notNullValue())
                .log().all();
    }
}