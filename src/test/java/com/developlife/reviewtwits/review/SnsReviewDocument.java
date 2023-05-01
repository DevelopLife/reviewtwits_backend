package com.developlife.reviewtwits.review;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Snippet;

import static com.developlife.reviewtwits.DocumentFormatProvider.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

/**
 * @author WhalesBob
 * @since 2023-04-03
 */
public class SnsReviewDocument {
    public static final Snippet AccessTokenHeader = requestHeaders(
            headerWithName("X-AUTH-TOKEN").description("access token").optional()
    );

    public static final Snippet ReviewIdAndSizeField = requestParameters(
            parameterWithName("reviewId").description("리뷰의 아이디").optional(),
            parameterWithName("size").attributes(required()).description("요청하는 페이지 사이즈")
    );

    public static final Snippet SnsReviewWriteRequestField = requestParts(
            partWithName("productURL").attributes(required())
                    .description("product 등록을 위한 URL 값입니다. http 혹은 https 로 시작하는 인터넷 URL 형식이어야 합니다."),
            partWithName("content").attributes(required())
                    .description("리뷰 글입니다. 10자 이상 입력해야 합니다."),
            partWithName("score").attributes(required())
                    .description("별점입니다. 0점부터 5점 사이의 정수로 입력할 수 있습니다"),
            partWithName("multipartImageFiles").attributes(required())
                    .description("리뷰에 등록하는 이미지 파일들입니다. 여러 장 등록할 수 있습니다."),
            partWithName("productName").attributes(required())
                    .description("검색에 등록된 리뷰 이름입니다.")
    );

    public static final Snippet SnsReviewChangeRequestField = requestParts(
            partWithName("content").description("리뷰 글입니다. 10자 이상 입력해야 합니다.").optional(),
            partWithName("score").description("별점입니다. 0점부터 5점 사이의 정수로 입력할 수 있습니다").optional(),
            partWithName("multipartImageFiles").description("리뷰에 등록하는 이미지 파일들입니다. 여러 장 등록할 수 있습니다.").optional(),
            partWithName("deleteFileList").description("삭제할 이미지 파일 이름입니다.").optional()
    );

    public static final Snippet ReviewIdField = pathParameters(
            parameterWithName("reviewId").attributes(required()).description("작성할 댓글의 리뷰 아이디")
    );

    public static final Snippet SnsCommentWriteRequestField = requestFields(
            fieldWithPath("content").type(JsonFieldType.STRING).attributes(required()).description("댓글 내용"),
            fieldWithPath("parentId").type(JsonFieldType.NUMBER).attributes(required()).description("부모 댓글 아이디")
    );

    public static final Snippet CommentIdField = pathParameters(
            parameterWithName("commentId").attributes(required())
                    .description("댓글의 아이디입니다.")
    );

    public static final Snippet SnsCommentChangeRequestField = requestParameters(
            parameterWithName("content").attributes(required())
                    .description("수정하고자 하는 댓글의 내용입니다.")
    );

    public static final Snippet SnsReactionAddRequestField = requestParameters(
            parameterWithName("reaction").attributes(required())
                    .description("추가하고자 하는 리액션 내용입니다.")
    );

    public static final Snippet SnsReviewFeedResponseField = responseFields(
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
            fieldWithPath("[].userInfo.isFollowed").type(JsonFieldType.BOOLEAN).description("요청한 유저가 팔로우했는지 여부").optional(),
            fieldWithPath("[].content").type(JsonFieldType.STRING).description("리뷰내용"),
            fieldWithPath("[].productUrl").type(JsonFieldType.STRING).description("제품 URL"),
            fieldWithPath("[].productName").type(JsonFieldType.STRING).description("제품이름"),
            fieldWithPath("[].score").type(JsonFieldType.NUMBER).description("별점"),
            fieldWithPath("[].reviewImageUrlList").type(JsonFieldType.ARRAY).description("리뷰이미지이름 리스트"),
            fieldWithPath("[].commentCount").type(JsonFieldType.NUMBER).description("댓글갯수"),
            fieldWithPath("[].reactionResponses").type(JsonFieldType.OBJECT).description("리액션"),
            fieldWithPath("[].isScrapped").type(JsonFieldType.BOOLEAN).description("스크랩여부")
    );

    public static final Snippet SnsReviewResultResponseField = responseFields(
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
            fieldWithPath("userInfo.isFollowed").type(JsonFieldType.BOOLEAN).description("요청한 유저가 팔로우했는지 여부").optional(),
            fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰내용"),
            fieldWithPath("productUrl").type(JsonFieldType.STRING).description("제품 URL"),
            fieldWithPath("productName").type(JsonFieldType.STRING).description("제품이름"),
            fieldWithPath("score").type(JsonFieldType.NUMBER).description("별점"),
            fieldWithPath("reviewImageUrlList").type(JsonFieldType.ARRAY).description("리뷰이미지이름 리스트"),
            fieldWithPath("commentCount").type(JsonFieldType.NUMBER).description("댓글갯수"),
            fieldWithPath("reactionResponses").type(JsonFieldType.OBJECT).description("리액션"),
            fieldWithPath("reactionResponses.GOOD.isReacted").type(JsonFieldType.BOOLEAN).description("리액션 여부"),
            fieldWithPath("reactionResponses.GOOD.count").type(JsonFieldType.NUMBER).description("리액션 갯수"),
            fieldWithPath("isScrapped").type(JsonFieldType.BOOLEAN).description("스크랩여부")
    );

    public static final Snippet SnsReviewCommentResponseField = responseFields(
        fieldWithPath("[].commentId").type(JsonFieldType.NUMBER).description("댓글아이디"),
        fieldWithPath("[].userInfo.userId").type(JsonFieldType.NUMBER).description("유저 DB 아이디"),
        fieldWithPath("[].userInfo.nickname").type(JsonFieldType.STRING).description("유저닉네임"),
        fieldWithPath("[].userInfo.accountId").type(JsonFieldType.STRING).description("유저 계정"),
        fieldWithPath("[].userInfo.introduceText").type(JsonFieldType.STRING).description("유저 한줄소개").optional(),
        fieldWithPath("[].userInfo.detailIntroduce").type(JsonFieldType.STRING).description("유저 한줄소개").optional(),
        fieldWithPath("[].userInfo.profileImageUrl").type(JsonFieldType.STRING).description("프로필이미지 파일이름").optional(),
        fieldWithPath("[].userInfo.reviewCount").type(JsonFieldType.NUMBER).description("유저작성 리뷰 수").optional(),
        fieldWithPath("[].userInfo.followers").type(JsonFieldType.NUMBER).description("팔로우 수").optional(),
        fieldWithPath("[].userInfo.followings").type(JsonFieldType.NUMBER).description("팔로잉 수").optional(),
        fieldWithPath("[].userInfo.isFollowed").type(JsonFieldType.BOOLEAN).description("요청한 유저가 팔로우했는지 여부").optional(),
        fieldWithPath("[].content").type(JsonFieldType.STRING).description("댓글내용"),
        fieldWithPath("[].parentCommentId").type(JsonFieldType.NUMBER).description("부모댓글아이디")
    );

    public static final Snippet SnsCommentResultResponseField = responseFields(
        fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("댓글아이디"),
        fieldWithPath("userInfo.userId").type(JsonFieldType.NUMBER).description("유저 DB 아이디"),
        fieldWithPath("userInfo.nickname").type(JsonFieldType.STRING).description("유저닉네임"),
        fieldWithPath("userInfo.accountId").type(JsonFieldType.STRING).description("유저 계정"),
        fieldWithPath("userInfo.introduceText").type(JsonFieldType.STRING).description("유저 한줄소개").optional(),
        fieldWithPath("userInfo.detailIntroduce").type(JsonFieldType.STRING).description("유저 한줄소개").optional(),
        fieldWithPath("userInfo.profileImageUrl").type(JsonFieldType.STRING).description("프로필이미지 파일이름").optional(),
        fieldWithPath("userInfo.reviewCount").type(JsonFieldType.NUMBER).description("유저작성 리뷰 수").optional(),
        fieldWithPath("userInfo.followers").type(JsonFieldType.NUMBER).description("팔로우 수").optional(),
        fieldWithPath("userInfo.followings").type(JsonFieldType.NUMBER).description("팔로잉 수").optional(),
        fieldWithPath("userInfo.isFollowed").type(JsonFieldType.BOOLEAN).description("요청한 유저가 팔로우했는지 여부").optional(),
        fieldWithPath("content").type(JsonFieldType.STRING).description("댓글내용"),
        fieldWithPath("parentCommentId").type(JsonFieldType.NUMBER).description("부모댓글아이디")
    );

    public static final Snippet SnsReactionResponseField = responseFields(
        fieldWithPath("reactionId").type(JsonFieldType.NUMBER).description("리액션아이디"),
        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 DB 아이디"),
        fieldWithPath("reviewId").type(JsonFieldType.NUMBER).description("리뷰아이디"),
        fieldWithPath("reactionType").type(JsonFieldType.STRING).description("리액션타입")
    );

    public static final Snippet SnsReviewScrapResultResponseField = responseFields(
        fieldWithPath("reviewScrapId").type(JsonFieldType.NUMBER).description("스크랩아이디"),
        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 DB 아이디"),
        fieldWithPath("reviewId").type(JsonFieldType.NUMBER).description("리뷰아이디")
    );

    public static final Snippet SnsCommentLikeResultResponseField = responseFields(
        fieldWithPath("commentLikeId").type(JsonFieldType.NUMBER).description("좋아요아이디"),
        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 DB 아이디"),
        fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("댓글아이디")
    );
}