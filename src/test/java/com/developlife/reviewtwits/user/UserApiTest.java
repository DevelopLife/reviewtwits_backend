package com.developlife.reviewtwits.user;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.CommonDocument;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.user.LoginUserRequest;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.repository.UserRepository;
import com.developlife.reviewtwits.service.user.UserService;
import com.developlife.reviewtwits.review.ShoppingMallReviewSteps;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.matcher.RestAssuredMatchers;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.mockito.Mockito.verify;

public class UserApiTest extends ApiTest {
    @Autowired
    private UserSteps userSteps;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

//    @Autowired
//    private AmazonS3 s3Client;

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
            .statusCode(HttpStatus.OK.value())
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
            .body("nickname", notNullValue())
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
                .filter(document(DEFAULT_RESTDOC_PATH, "201 정상적으로 회원가입이 이루어짐<br> 400 입력에 문제가 있음<br> 409 입력한 값이 중복(유니크한값이여야함)", "회원가입", UserDocument.RegisterUserRequestField, UserDocument.JwtTokenResponseField))
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
    @DisplayName("회원가입 추가정보 등록 성공")
    void 회원가입추가정보_입력정보_200() throws IOException {
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());
        MultiPartSpecification profileImage = ShoppingMallReviewSteps.프로필_이미지_파일정보생성();

        given(this.spec)
            .filter(document(DEFAULT_RESTDOC_PATH, "200 정상적으로 입력이 들어갔을때<br>400 비정상적인 입력<br>409 닉네임이 중복", "회원가입 추가정보 등록",
                UserDocument.RegisterUserInfoRequestField, UserDocument.UserDetailInfoResponseField))
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .header("X-AUTH-TOKEN", token)
            .multiPart("nickname", "test")
            .multiPart("introduceText", "test")
            .multiPart(profileImage)
        .when()
            .post("/users/register-addition")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("nickname", equalTo("test"))
            .body("introduceText", equalTo("test"))
            .log().all().extract().response();

    //    verify(s3Client,Mockito.times(1)).putObject(Mockito.any(PutObjectRequest.class));
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

    @Test
    void 회원가입_프로필이미지_업로드() throws IOException {

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,"프로필 이미지를 업로드하는 API입니다. " +
                        "<br>정상적으로 업로드 되었다면 200 OK 를 받을 수 있습니다." +
                        "<br>등록된 이미지 파일 확장자가 아닌 다른 확장자를 입력하면, 400 Bad Request 가 반환됩니다." +
                        "<br>이미지 multipart/form-data 는 필수값입니다. 입력하지 않을 시 400 Bad Request 가 반환됩니다." +
                        "<br>유저의 토큰 값 역시 필수값입니다. 입력하지 않을 시 403 Forbidden 이 반환됩니다.",
                        "프로필이미지업로드", UserDocument.AccessTokenHeader,UserDocument.ImageUpdateRequestField))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN",token)
                .multiPart(UserSteps.프로필_이미지_파일정보_생성())
                .when()
                .post("/users/save-profile-image")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all();

        assertThat(유저정보_프로필이미지_추출()).isNotNull();
    //    verify(s3Client,Mockito.times(1)).putObject(Mockito.any(PutObjectRequest.class));
    }

    private String 유저정보_프로필이미지_추출() {
        User user = userRepository.findByAccountId(registerUserRequest.accountId()).get();

        ExtractableResponse<Response> response = given(this.spec)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("userId", user.getUserId())
                .when()
                .get("/users/search/{userId}")
                .then()
                .log().all().extract();

        return response.jsonPath().get("profileImage");
    }

    @Test
    void 회원가입_프로필이미지_이미지아닌_업로드() throws IOException {

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN",token)
                .multiPart(UserSteps.프로필_이미지아닌_파일정보_생성())
                .when()
                .post("/users/save-profile-image")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("find{it.message == '입력된 파일이 이미지가 아닙니다.' " +
                        "&& it.errorType == 'ImageFile' && it.fieldName == 'imageFile' }",notNullValue())
                .log().all();

    //    verify(s3Client,Mockito.times(0)).putObject(Mockito.any(PutObjectRequest.class));
    }

    @Test
    void 회원가입_프로필이미지_업로드안함() {

        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("X-AUTH-TOKEN",token)
                .when()
                .post("/users/save-profile-image")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("find{it.errorType == 'FileUploadException' && it.fieldName == 'file' }",notNullValue())
                .log().all();
    }

    @Test
    void 유저프로필_상세소개_수정_성공_200(){
        final String token = userSteps.로그인액세스토큰정보(UserSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,"유저 프로필에서 상세 소개를 변경하는 API 입니다." +
                        "header 에 X-AUTH-TOKEN 을, body 에 소개 내용만을 넣어 주면, 200 OK 와 함께 유저의 상세 소개가 변경됩니다." +
                        "잘못된 X-AUTH-TOKEN 을 입력하거나, token을 입력하지 않았을 경우, 403 에러가 반환됩니다."
                        ,"유저프로필상세소개수정요청",UserDocument.AccessTokenHeader))
                .config(config().encoderConfig(encoderConfig()
                        .encodeContentTypeAs("text/plain", ContentType.TEXT)
                        .defaultContentCharset("UTF-8")))
                .header("X-AUTH-TOKEN",token)
                .body(UserSteps.userDetailIntroduce)
                .when()
                .post("/users/change-detail-messages")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all();

        User user = userRepository.findByAccountId(UserSteps.accountId).get();
        assertThat(user.getDetailIntroduce()).isEqualTo(UserSteps.userDetailIntroduce);
    }

    @Test
    void 유저프로필_상세소개_수정_헤더정보없음_403(){
        given(this.spec)
                .config(config().encoderConfig(encoderConfig()
                        .encodeContentTypeAs("text/plain", ContentType.TEXT)
                        .defaultContentCharset("UTF-8")))
                .body(UserSteps.userDetailIntroduce)
                .when()
                .post("/users/change-detail-messages")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all();
    }
}
