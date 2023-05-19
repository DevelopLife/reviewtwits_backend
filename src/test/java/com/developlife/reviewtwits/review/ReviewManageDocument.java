package com.developlife.reviewtwits.review;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Snippet;

import static com.developlife.reviewtwits.DocumentFormatProvider.required;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

/**
 * @author WhalesBob
 * @since 2023-05-19
 */
public class ReviewManageDocument {
    public static final Snippet reviewApproveRequestField =  requestFields(
            fieldWithPath("reviewId").type(JsonFieldType.NUMBER).attributes(required()).description("리뷰 아이디"),
            fieldWithPath("approveType").type(JsonFieldType.STRING).attributes(required()).description("리뷰 허가 타입")
    );

    public static final Snippet reviewApproveResponseField = responseFields(
            fieldWithPath("reviewId").type(JsonFieldType.NUMBER).description("리뷰 아이디"),
            fieldWithPath("status").type(JsonFieldType.STRING).description("리뷰 상태")
    );
}