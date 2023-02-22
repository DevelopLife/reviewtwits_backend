package com.developlife.reviewtwits.user;

import com.developlife.reviewtwits.message.request.RegisterUserRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class UserSteps {
    public static ExtractableResponse<Response> 회원가입요청(final RegisterUserRequest request) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/user")
                .then()
                .log().all().extract();
    }

    public static RegisterUserRequest 회원가입요청_생성() {
        final String username = "ghdic";

        return new RegisterUserRequest(username);
    }
}
