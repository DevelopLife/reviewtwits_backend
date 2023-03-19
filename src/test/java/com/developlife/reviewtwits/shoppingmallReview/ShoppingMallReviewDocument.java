package com.developlife.reviewtwits.shoppingmallReview;

import org.springframework.restdocs.snippet.Snippet;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static com.developlife.reviewtwits.DocumentFormatProvider.*;
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
}