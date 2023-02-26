package com.developlife.reviewtwits.user;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.controller.UserController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserApiTest extends ApiTest {

    private UserController userController;



    @Test
    @DisplayName("특정유저조회")
    void 특정유저조회_유저정보확인_True() {
        final var request = UserSteps.유저정보요청()
    }

    @Test
    @DisplayName("로그인성공")
    void 로그인성공_로그인정보확인_True() {

    }

    @Test
    @DisplayName("로그인실패")
    void 로그인실패_로그인정보불일치_False() {
        // 아이디가 존재하지 않음
        // 비밀번호 불일치

    }

    @Test
    @DisplayName("회원가입 성공")
    void 회원가입체크_회원가입정보저장확인_True() {
        final var request = UserSteps.회원가입요청_생성();

        final var response = UserSteps.회원가입요청(request);

        assertThat(response.jsonPath().getString("userId")).isEqualTo("1");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("회원가입 실패 - 입력정보부족")
    void 회원가입체크_입력정보부족_False() {

    }

    @Test
    @DisplayName("회원가입 실패 - 입력한 정보가 조건에 맞지않음")
    void 회원가입체크_입력조건부적합_False() {
        // 대소문자 구분
        // 6글자 이상
        // 알파벳, 숫자, 특수문자 1개이상 들어가있는지

    }

    @Test
    @DisplayName("유저 권한 확인")
    void 유저권한확인_유저권학부여확인_True() {

    }

    @Test
    @DisplayName("JWT토큰생성확인")
    void JWT토큰생성확인_토큰존재여부_True() {

    }

    @Test
    @DisplayName("JWT토큰인증확인")
    void JWT토큰인증확인_인증여부_True() {

    }
}
