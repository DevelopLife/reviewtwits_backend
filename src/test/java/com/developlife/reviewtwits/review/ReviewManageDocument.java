package com.developlife.reviewtwits.review;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Snippet;

import static com.developlife.reviewtwits.DocumentFormatProvider.required;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

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

    public static final Snippet reviewSearchRequestField = requestParameters(
            parameterWithName("size").attributes(required()).description("요청하는 페이지 사이즈"),
            parameterWithName("reviewId").description("리뷰 아이디").optional(),
            parameterWithName("status").description("리뷰 상태").optional(),
            parameterWithName("sort").description("리뷰 오름/내림차순").optional(),
            parameterWithName("startDate").description("리뷰 시작 날짜").optional(),
            parameterWithName("endDate").description("리뷰 종료 날짜").optional()
    );
}