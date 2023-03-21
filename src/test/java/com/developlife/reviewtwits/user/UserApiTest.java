package com.developlife.reviewtwits.user;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.CommonDocument;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.user.LoginUserRequest;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.repository.UserRepository;
import com.developlife.reviewtwits.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.matcher.RestAssuredMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;

public class UserApiTest extends ApiTest {
    @Autowired
    private UserSteps userSteps;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private RegisterUserRequest registerUserRequest;
    private RegisterUserRequest registerAdminRequest;

    @BeforeEach
    void setting() {
        registerUserRequest = userSteps.회원가입정보_생성();
        registerAdminRequest = userSteps.회원가입정보_어드민_생성();

        // 일반유저, 어드민유저 회원가입 해두고 테스트 진행
        userService.register(registerUserRequest, UserSteps.일반유저권한_생성());
        userService.register(registerAdminRequest, UserSteps.어드민유저권한_생성());
    }

    @Test
    @DisplayName("특정유저조회")
    void 특정유저조회_유저정보확인_200() {
        User user = userRepository.findByAccountId(registerUserRequest.accountId()).get();

        given(this.spec)
            .filter(document(DEFAULT_RESTDOC_PATH, "특정유저의 공개가능한 정보를 조회합니다", "특정유저조회", UserDocument.UserInfoPathParams, UserDocument.UserInfoResponseField))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("userId", user.getUserId())
        .when()
            .get("/users/search/{userId}")
        .then()
            .assertThat()
            .statusCode(HttpStatus.ALREADY_REPORTED.value())
            // 기본정보 표시
            .body("nickname", notNullValue())
            .body("accountId", notNullValue())
            // 민감정보 노출 x
            .body("phoneNumber", nullValue())
            .log().all();
    }

    @Test
    @DisplayName("자신정보조회")
    void 자신정보조회_유저정보확인_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
            .filter(document(DEFAULT_RESTDOC_PATH, "자신의 디테일한 정보를 조회합니다", "자신정보조회", CommonDocument.AccessTokenHeader, UserDocument.UserDetailInfoResponseField))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("X-AUTH-TOKEN", token)
        .when()
            .get("/users/me")
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            // 기본정보 표시
            .body("nickname", equalTo(registerUserRequest.nickname()))
            // 민감정보 노출 o
            .body("phoneNumber", equalTo(registerUserRequest.phoneNumber()))
            // 비밀번호는 예외
            .body("accountPw", nullValue())
            .log().all();
    }

    @Test
    @DisplayName("로그인인성공")
    void 로그인성공_로그인정보확인_200() {
        LoginUserRequest request = UserSteps.로그인요청생성();

        given(this.spec).log().all()
            .filter(document(DEFAULT_RESTDOC_PATH,"로그인시 accessToken과 refreshToken이 발급됩니다", "로그인", UserDocument.LoginUserRequestField, UserDocument.JwtTokenResponseField))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
        .when()
            .post("/users/login")
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .cookie("refreshToken", notNullValue())
            .cookie("refreshToken", RestAssuredMatchers.detailedCookie().httpOnly(true))
//            .cookie("refreshToken", RestAssuredMatchers.detailedCookie().secured(true))
            .body("accessToken", notNullValue())
            .log().all().extract();
    }

    @Test
    @DisplayName("로그인실패 - 아이디불일치")
    void 로그인실패_아이디불일치_401() {
        final var request = UserSteps.로그인요청_생성_아이디불일치();

        given(this.spec).log().all()
            .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
        .when()
            .post("/users/login")
        .then()
            .assertThat()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .body("find{it.errorType == 'AccountIdNotFoundException' " +
                    "&& it.fieldName == 'accountId'}", notNullValue())
            .log().all().extract();
    }

    @Test
    @DisplayName("로그인실패 - 비밀번호 불일치")
    void 로그인실패_비밀번호불일치_401() {
        final var request = UserSteps.로그인요청_생성_비밀번호불일치();

        given(this.spec).log().all()
            .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
        .when()
            .post("/users/login")
        .then()
            .assertThat()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .body("find{it.errorType == 'AccountPasswordWrongException' " +
                    "&& it.fieldName == 'accountPw'}", notNullValue())
            .log().all().extract();
    }

    @Test
    @DisplayName("회원가입 성공")
    void 회원가입체크_회원가입정보저장확인_201() {
        final var request = userSteps.추가회원가입정보_생성();

        // 회원가입
        var response = given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "회원가입을 할때 사용", "회원가입", UserDocument.RegisterUserRequestField, UserDocument.JwtTokenResponseField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
            .when()
                .post("/users/register")
            .then()
                .statusCode(HttpStatus.CREATED.value())
                .cookie("refreshToken", notNullValue())
                .cookie("refreshToken", RestAssuredMatchers.detailedCookie().httpOnly(true))
//                .cookie("refreshToken", RestAssuredMatchers.detailedCookie().secured(true))
                .body("accessToken", notNullValue())
                .log().all().extract().response();

        // 회원가입 정보와 저장된 정보 비교
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("X-AUTH-TOKEN", response.body().path("accessToken"))
        .when()
            .get("/users/me")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("nickname", equalTo("add_"+ UserSteps.nickname))
            .body("accountId", equalTo("add_" + UserSteps.accountId))
            .body("accountPw", nullValue())
            .body("birthDate", equalTo(UserSteps.birthDate))
            .body("phoneNumber", equalTo("01011110000"))
            .body("gender", equalTo(UserSteps.gender.name()))
            .log().all().extract().response();

    }

    @Test
    @DisplayName("회원가입 실패 - 입력정보부족")
    void 회원가입체크_입력정보부족_400() {
        final var request = UserSteps.회원가입요청_입력정보_누락();

        given(this.spec)
            .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
        .when()
            .post("/users/register")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("collect{ it.fieldName }", hasItems("phoneNumber", "accountPw", "verifyCode", "accountId"))
            .log().all().extract().response();
    }

    @Test
    @DisplayName("회원가입 실패 - 입력한 정보가 조건에 맞지않음")
    void 회원가입체크_입력조건부적합_400() {

        // 이메일 인증 코드 invalid
        // 비밀번호 조건 틀린
        final var request = UserSteps.회원가입요청_입력정보_부적합();
        given(this.spec)
            .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
        .when()
            .post("/users/register")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("collect{ it.fieldName }", hasItems("accountPw", "phoneNumber"))
            .log().all().extract().response();
    }

    @Test
    @DisplayName("로그아웃")
    void 로그아웃_캐시제거_200() {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
            .filter(document(DEFAULT_RESTDOC_PATH, "로그아웃은 서버에 보관된 refreshToken을 폐기하고 쿠키에 저장된 refreshToken을 제거합니다\n accessToken은 클라이언트에서 따로 제거 해야합니다", "로그아웃", CommonDocument.AccessTokenHeader))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("X-AUTH-TOKEN", token)
        .when()
            .post("/users/logout")
        .then()
            .statusCode(HttpStatus.OK.value())
            // 쿠키 초기화 확인
            .cookie("refreshToken", RestAssuredMatchers.detailedCookie().maxAge(0))
            .log().all().extract();
    }

    @Test
    @DisplayName("accessToken 갱신")
    void 액세스토큰갱신_갱신완료_200() {
        final String refreshToken = userSteps.로그인리프래시토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
            .filter(document(DEFAULT_RESTDOC_PATH,
                "Refreesh Tokn으로 Access Token 갱신<br>Refresh Token은 브라우저 쿠키를 통해 자동으로 넘어갑니다",
                "Access Token 갱신",
                UserDocument.JwtTokenResponseField))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .cookie("refreshToken", refreshToken)
        .when()
            .post("/users/issue/access-token")
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("accessToken", notNullValue())
            .log().all();
    }
}
