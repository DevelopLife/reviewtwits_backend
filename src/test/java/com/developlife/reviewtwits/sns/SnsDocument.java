package com.developlife.reviewtwits.sns;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Snippet;

import static com.developlife.reviewtwits.DocumentFormatProvider.required;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.*;


/**
 * @author WhalesBob
 * @since 2023-03-20
 */
public class SnsDocument {
    public static final Snippet followRequestField = requestFields(
            fieldWithPath("targetUserAccountId").type(JsonFieldType.STRING)
                    .attributes(required()).description("팔로우하려는 계정의 accountId, 이메일 형식이어야 합니다.")
    );

    public static final Snippet followIdField = pathParameters(
            parameterWithName("accountId").attributes(required()).description("유저 계정 아이디")
    );

    public static final Snippet snsFollowResponseField = responseFields(
            fieldWithPath("[].nickname").type(JsonFieldType.STRING).description("유저닉네임"),
            fieldWithPath("[].accountId").type(JsonFieldType.STRING).description("유저 계정"),
            fieldWithPath("[].introduceText").type(JsonFieldType.STRING).description("유저 한줄소개").optional(),
            fieldWithPath("[].profileImage").type(JsonFieldType.STRING).description("프로필이미지 파일이름").optional()
    );

    public static final Snippet SearchAllSnsRequest = requestParameters(
        parameterWithName("searchKey").attributes(required()).description("검색어(2-20글자)")
    );

    public static final Snippet SearchAllSnsResponse = responseFields(
        fieldWithPath("itemList.[].itemId").type(JsonFieldType.NUMBER).description("아이템아이디"),
        fieldWithPath("itemList.[].productName").type(JsonFieldType.STRING).description("상품이름"),
        fieldWithPath("itemList.[].productImageUrl").type(JsonFieldType.STRING).description("상품이미지URL"),
        fieldWithPath("itemList.[].score").type(JsonFieldType.STRING).description("평균평점"),
        fieldWithPath("itemList.[].url").type(JsonFieldType.STRING).description("상품정보페이지URL"),
        fieldWithPath("reviewList.[].reviewId").type(JsonFieldType.NUMBER).description("리뷰아이디"),
        fieldWithPath("reviewList.[].createdDate").type(JsonFieldType.STRING).description("생성일"),
        fieldWithPath("reviewList.[].lastModifiedDate").type(JsonFieldType.STRING).description("최근수정일"),
        fieldWithPath("reviewList.[].userInfo.nickname").type(JsonFieldType.STRING).description("닉네임"),
        fieldWithPath("reviewList.[].userInfo.accountId").type(JsonFieldType.STRING).description("아이디"),
        fieldWithPath("reviewList.[].userInfo.introduceText").type(JsonFieldType.STRING).description("자기소개"),
        fieldWithPath("reviewList.[].userInfo.profileImage").type(JsonFieldType.STRING).description("프로필이미지URL"),
        fieldWithPath("reviewList.[].content").type(JsonFieldType.STRING).description("리뷰콘텐츠"),
        fieldWithPath("reviewList.[].productUrl").type(JsonFieldType.STRING).description("리뷰상품구매URL"),
        fieldWithPath("reviewList.[].productName").type(JsonFieldType.STRING).description("리뷰상품이름"),
        fieldWithPath("reviewList.[].score").type(JsonFieldType.NUMBER).description("평점"),
        fieldWithPath("reviewList.[].reactionResponses.[]").type(JsonFieldType.STRING).description("리뷰이미지")
    );
}