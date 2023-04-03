package com.developlife.reviewtwits.review;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Snippet;

import static com.developlife.reviewtwits.DocumentFormatProvider.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
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

    public static final Snippet ReviewIdField = pathParameters(
            parameterWithName("reviewId").attributes(required()).description("작성할 댓글의 리뷰 아이디")
    );

    public static final Snippet SnsCommentWriteRequestField = requestFields(
            fieldWithPath("content").type(JsonFieldType.STRING).attributes(required()).description("댓글 내용"),
            fieldWithPath("parentId").type(JsonFieldType.NUMBER).attributes(required()).description("부모 댓글 아이디")
    );

    public static final Snippet CommentIdField = pathParameters(
            parameterWithName("commentId").attributes(required())
                    .description("수정 또는 삭제할 댓글의 아이디입니다.")
    );

    public static final Snippet SnsCommentChangeRequestField = requestParameters(
            parameterWithName("content").attributes(required())
                    .description("수정하고자 하는 댓글의 내용입니다.")
    );

    public static final Snippet SnsReactionAddRequestField = requestParameters(
            parameterWithName("reaction").attributes(required())
                    .description("추가하고자 하는 리액션 내용입니다.")
    );
}