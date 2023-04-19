package com.developlife.reviewtwits.review;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Snippet;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static com.developlife.reviewtwits.DocumentFormatProvider.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;


/**
 * @author WhalesBob
 * @since 2023-03-14
 */
public class ShoppingMallReviewDocument {
    public static final Snippet ShoppingMallReviewWriteRequestField = requestParts(
            partWithName("productURL").attributes(required())
                    .description("product 등록을 위한 URL 값입니다. http 혹은 https 로 시작하는 인터넷 URL 형식이어야 합니다."),
            partWithName("content").attributes(required())
                    .description("리뷰 글입니다. 10자 이상 입력해야 합니다."),
            partWithName("score").attributes(required())
                    .description("별점입니다. 0점부터 5점 사이의 정수로 입력할 수 있습니다"),
            partWithName("multipartImageFiles").description("리뷰에 등록하는 이미지 파일들입니다. 여러 장 등록할 수 있습니다.")
    );

    public static final Snippet ReviewProductRequestHeader =  requestHeaders(
            headerWithName("productURL").attributes(required()).description("product URL 을 받는 곳입니다.")
    );

    public static final Snippet ReviewIdField = pathParameters(
            parameterWithName("reviewId").attributes(required()).description("리뷰의 아이디")
    );

    public static final Snippet contentField = requestParts(
            partWithName("content").description("리뷰 글입니다. 10자 이상 입력해야 합니다.")
    );

    public static final Snippet scoreField = requestParts(
            partWithName("score").description("별점입니다. 0점부터 5점 사이의 정수로 입력할 수 있습니다")
    );

    public static final Snippet imageFileFiend = requestParts(
            partWithName("multipartImageFiles").description("리뷰에 등록하는 이미지 파일들입니다. 여러 장 등록할 수 있습니다.")
    );

    public static final Snippet deleteFileListField = requestParts(
            partWithName("deleteFileList").description("리뷰에서 삭제하고자 하는 파일의 이름 리스트입니다. 여러 개 등록할 수 있습니다." +
                    " 해당 이름으로 된 파일이 존재하지 않는 경우, 파일의 삭제 처리가 이루어지지 않습니다.")
    );

    public static final Snippet shoppingMallReviewInfoResponseField =  responseFields(
            fieldWithPath("averageStarScore").type(JsonFieldType.NUMBER).description("평균별점"),
            fieldWithPath("totalReviewCount").type(JsonFieldType.NUMBER).description("전체 리뷰 갯수"),
            fieldWithPath("recentReviewCount").type(JsonFieldType.NUMBER).description("최근 리뷰 갯수"),
            fieldWithPath("starScoreArray").type(JsonFieldType.ARRAY).description("별점 분포 퍼센트 배열")
    );

    public static final Snippet shoppingMallReviewListResponseField = responseFields(
            fieldWithPath("[].createdDate").type(JsonFieldType.ARRAY).description("생성시간"),
            fieldWithPath("[].lastModifiedDate").type(JsonFieldType.ARRAY).description("마지막수정시간"),
            fieldWithPath("[].reviewId").type(JsonFieldType.NUMBER).description("리뷰 아이디"),
            fieldWithPath("[].userInfo.userId").type(JsonFieldType.NUMBER).description("유저 DB 아이디"),
            fieldWithPath("[].userInfo.nickname").type(JsonFieldType.STRING).description("유저닉네임"),
            fieldWithPath("[].userInfo.accountId").type(JsonFieldType.STRING).description("유저 계정"),
            fieldWithPath("[].userInfo.introduceText").type(JsonFieldType.STRING).description("유저 한줄소개").optional(),
            fieldWithPath("[].userInfo.detailIntroduce").type(JsonFieldType.STRING).description("유저 상세소개").optional(),
            fieldWithPath("[].userInfo.profileImageUrl").type(JsonFieldType.STRING).description("프로필이미지 파일이름").optional(),
            fieldWithPath("[].userInfo.reviewCount").type(JsonFieldType.NUMBER).description("유저작성 리뷰 수").optional(),
            fieldWithPath("[].userInfo.followers").type(JsonFieldType.NUMBER).description("팔로우 수").optional(),
            fieldWithPath("[].userInfo.followings").type(JsonFieldType.NUMBER).description("팔로잉 수").optional(),
            fieldWithPath("[].projectId").type(JsonFieldType.NUMBER).description("프로젝트 아이디"),
            fieldWithPath("[].content").type(JsonFieldType.STRING).description("리뷰내용"),
            fieldWithPath("[].productUrl").type(JsonFieldType.STRING).description("제품 URL"),
            fieldWithPath("[].productName").type(JsonFieldType.STRING).description("제품이름").optional(),
            fieldWithPath("[].score").type(JsonFieldType.NUMBER).description("별점"),
            fieldWithPath("[].reviewImageUrlList").type(JsonFieldType.ARRAY).description("리뷰이미지이름 리스트"),
            fieldWithPath("[].exist").type(JsonFieldType.BOOLEAN).description("존재여부")
    );

    public static final Snippet shoppingMallReviewResponseField = responseFields(
            fieldWithPath("createdDate").type(JsonFieldType.ARRAY).description("생성시간"),
            fieldWithPath("lastModifiedDate").type(JsonFieldType.ARRAY).description("마지막수정시간"),
            fieldWithPath("reviewId").type(JsonFieldType.NUMBER).description("리뷰 아이디"),
            fieldWithPath("userInfo.userId").type(JsonFieldType.NUMBER).description("유저 DB 아이디"),
            fieldWithPath("userInfo.nickname").type(JsonFieldType.STRING).description("유저닉네임"),
            fieldWithPath("userInfo.accountId").type(JsonFieldType.STRING).description("유저 계정"),
            fieldWithPath("userInfo.introduceText").type(JsonFieldType.STRING).description("유저 한줄소개").optional(),
            fieldWithPath("userInfo.detailIntroduce").type(JsonFieldType.STRING).description("유저 상세소개").optional(),
            fieldWithPath("userInfo.profileImageUrl").type(JsonFieldType.STRING).description("프로필이미지 파일이름").optional(),
            fieldWithPath("userInfo.reviewCount").type(JsonFieldType.NUMBER).description("유저작성 리뷰 수").optional(),
            fieldWithPath("userInfo.followers").type(JsonFieldType.NUMBER).description("팔로우 수").optional(),
            fieldWithPath("userInfo.followings").type(JsonFieldType.NUMBER).description("팔로잉 수").optional(),
            fieldWithPath("projectId").type(JsonFieldType.NUMBER).description("프로젝트 아이디"),
            fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰내용"),
            fieldWithPath("productUrl").type(JsonFieldType.STRING).description("제품 URL"),
            fieldWithPath("productName").type(JsonFieldType.STRING).description("제품이름").optional(),
            fieldWithPath("score").type(JsonFieldType.NUMBER).description("별점"),
            fieldWithPath("reviewImageUrlList").type(JsonFieldType.ARRAY).description("리뷰이미지이름 리스트"),
            fieldWithPath("exist").type(JsonFieldType.BOOLEAN).description("존재여부")
    );
}