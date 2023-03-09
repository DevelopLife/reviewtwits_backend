package com.developlife.reviewtwits.email;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Snippet;

import static com.developlife.reviewtwits.DocumentFormatProvider.required;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

/**
 * @author ghdic
 * @since 2023/03/08
 */
public class EmailDocument {
    public static final Snippet FindIdsEmailRequestField = requestFields(
            fieldWithPath("phoneNumber").type(JsonFieldType.STRING).attributes(required()).description("아이디"),
            fieldWithPath("birthDate").type(JsonFieldType.STRING).attributes(required()).description("생일 yyyy-MM-dd")
    );
    public static final Snippet FindIdsEmailResponseField = responseFields(
            fieldWithPath("[].nickname").type(JsonFieldType.STRING).attributes(required()).description("닉네임"),
            fieldWithPath("[].accountId").type(JsonFieldType.STRING).attributes(required()).description("아이디"),
            fieldWithPath("[].createdDate").type(JsonFieldType.STRING).attributes(required()).description("계정생성일")
    );
    public static final Snippet FindPwEmailRequestField = requestFields(
            fieldWithPath("accountId").type(JsonFieldType.STRING).attributes(required()).description("아이디"),
            fieldWithPath("phoneNumber").type(JsonFieldType.STRING).attributes(required()).description("휴대폰번호"),
            fieldWithPath("birthDate").type(JsonFieldType.STRING).attributes(required()).description("생일 yyyy-MM-dd")
    );

    public static final Snippet ResetPwEmailRequestField = requestFields(
            fieldWithPath("accountPw").type(JsonFieldType.STRING).attributes(required()).description("초기화할 비밀번호"),
            fieldWithPath("verifyCode").type(JsonFieldType.STRING).attributes(required()).description("URL로부터 받은 code")
    );
}
