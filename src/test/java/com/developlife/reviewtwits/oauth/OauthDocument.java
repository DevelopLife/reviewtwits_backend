package com.developlife.reviewtwits.oauth;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Snippet;

import static com.developlife.reviewtwits.DocumentFormatProvider.*;
import static com.developlife.reviewtwits.DocumentFormatProvider.required;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

/**
 * @author ghdic
 * @since 2023/03/15
 */
public class OauthDocument {
    public static final Snippet RegisterOauthUserRequestField = requestFields(
        fieldWithPath("nickname").type(JsonFieldType.STRING).attributes(required()).attributes(required()).description("닉네임 2-20글자"),
        fieldWithPath("birthDate").type(JsonFieldType.STRING).description("생일").optional(),
        fieldWithPath("phoneNumber").type(JsonFieldType.STRING).attributes(required()).description("전화번호 01000000000"),
        fieldWithPath("gender").type(JsonFieldType.STRING).description("성별(남자, 여자)").optional(),
        fieldWithPath("provider").type(JsonFieldType.STRING).attributes(required()).description("Oauth 제공자(GOOGLE, KAKAO, NAVER, REVIEWTWITS)")
    );
}
