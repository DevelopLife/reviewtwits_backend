package com.developlife.reviewtwits.statistics;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Snippet;

import static com.developlife.reviewtwits.DocumentFormatProvider.required;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

/**
 * @author WhalesBob
 * @since 2023-04-23
 */
public class StatDocument {
    public static final Snippet AccessTokenHeader = requestHeaders(
            headerWithName("X-AUTH-TOKEN").description("access token").optional()
    );
    public static final Snippet statMessageRequestField = requestFields(
            fieldWithPath("inflowUrl").type(JsonFieldType.STRING).attributes(required()).description("검색 유입 URL"),
            fieldWithPath("productUrl").type(JsonFieldType.STRING).attributes(required()).description("상품 URL"),
            fieldWithPath("device").type(JsonFieldType.STRING).attributes(required()).description("디바이스 정보")
    );

    public static final Snippet savedStatResponseField = responseFields(
            fieldWithPath("statId").type(JsonFieldType.NUMBER).description("통계정보 아이디"),
            fieldWithPath("createdDate").type(JsonFieldType.STRING).description("통계 생성 날짜"),
            fieldWithPath("inflowUrl").type(JsonFieldType.STRING).description("검색 유입 URL"),
            fieldWithPath("productUrl").type(JsonFieldType.STRING).description("상품 URL"),
            fieldWithPath("userInfo").type(JsonFieldType.VARIES).description("유저 정보"),
            fieldWithPath("userInfo.userId").type(JsonFieldType.NUMBER).description("유저 아이디").optional(),
            fieldWithPath("userInfo.nickname").type(JsonFieldType.STRING).description("유저 닉네임").optional(),
            fieldWithPath("userInfo.accountId").type(JsonFieldType.STRING).description("유저 계정 아이디").optional(),
            fieldWithPath("userInfo.introduceText").type(JsonFieldType.STRING).description("유저 한줄소개").optional(),
            fieldWithPath("userInfo.profileImageUrl").type(JsonFieldType.STRING).description("유저 프로필 이미지").optional(),
            fieldWithPath("userInfo.detailIntroduce").type(JsonFieldType.STRING).description("유저 세부소개").optional(),
            fieldWithPath("userInfo.reviewCount").type(JsonFieldType.NUMBER).description("리뷰 숫자").optional(),
            fieldWithPath("userInfo.followers").type(JsonFieldType.NUMBER).description("팔로워 숫자").optional(),
            fieldWithPath("userInfo.followings").type(JsonFieldType.NUMBER).description("팔로잉 숫자").optional(),
            fieldWithPath("projectId").type(JsonFieldType.NUMBER).description("프로젝트 아이디"),
            fieldWithPath("productId").type(JsonFieldType.NUMBER).description("상품 아이디"),
            fieldWithPath("deviceInfo").type(JsonFieldType.STRING).description("디바이스 정보")
    );
}