package com.developlife.reviewtwits.user;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Snippet;

import static com.developlife.reviewtwits.DocumentFormatProvider.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
/**
 * @author ghdic
 * @since 2023/03/06
 */
public class UserDocument {
    public static final Snippet UserInfoPathParams = pathParameters(
            parameterWithName("userId").description("유저아이디")
    );

    public static final Snippet UserInfoResponseField = responseFields(
            fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
            fieldWithPath("accountId").type(JsonFieldType.STRING).description("아이디")
    );

    public static final Snippet UserDetailInfoResponseField = responseFields(
            fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
            fieldWithPath("accountId").type(JsonFieldType.STRING).description("아이디"),
            fieldWithPath("birthDate").type(JsonFieldType.STRING)
                    .attributes(getDateFormat()).description("생일").optional(),
            fieldWithPath("phoneNumber").type(JsonFieldType.STRING)
                    .attributes(getPhoneNumberFormat()).description("전화번호"),
            fieldWithPath("gender").type(JsonFieldType.STRING)
                    .attributes(getGenderFormat()).description("성별").optional(),
            fieldWithPath("provider").type(JsonFieldType.STRING).description("Oauth 가입 제공자").optional(),
            fieldWithPath("uuid").type(JsonFieldType.STRING).description("Oauth Sub값").optional()
    );
    public static final Snippet RegisterUserRequestField = requestFields(
            fieldWithPath("nickname").type(JsonFieldType.STRING).attributes(required()).description("닉네임"),
            fieldWithPath("accountId").type(JsonFieldType.STRING).attributes(required()).description("아이디"),
            fieldWithPath("accountPw").type(JsonFieldType.STRING).attributes(required()).description("비밀번호"),
            fieldWithPath("birthDate").type(JsonFieldType.STRING)
                    .attributes(getDateFormat()).description("생일"),
            fieldWithPath("phoneNumber").type(JsonFieldType.STRING)
                    .attributes(getPhoneNumberFormat()).attributes(required()).description("전화번호"),
            fieldWithPath("gender").type(JsonFieldType.STRING)
                    .attributes(getGenderFormat()).description("성별(남자, 여자)"),
            fieldWithPath("verifyCode").type(JsonFieldType.STRING).attributes(required()).description("이메일 인증코드")
    );
    public static final Snippet JwtTokenResponseField = responseFields(
            fieldWithPath("accessToken").type(JsonFieldType.STRING).description("access token"),
            fieldWithPath("tokenType").type(JsonFieldType.STRING).description("토큰 타입"),
            fieldWithPath("provider").type(JsonFieldType.STRING).description("제공자")
    );

    public static final Snippet LoginUserRequestField = requestFields(
            fieldWithPath("accountId").type(JsonFieldType.STRING).attributes(required()).description("아이디"),
            fieldWithPath("accountPw").type(JsonFieldType.STRING).attributes(required()).description("비밀번호")
    );

    public static final Snippet AccessTokenHeader = requestHeaders(
            headerWithName("X-AUTH-TOKEN").attributes(required()).description("access token")
    );
}
