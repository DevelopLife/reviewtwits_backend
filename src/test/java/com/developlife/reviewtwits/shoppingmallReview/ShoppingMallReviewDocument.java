package com.developlife.reviewtwits.shoppingmallReview;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Snippet;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static com.developlife.reviewtwits.DocumentFormatProvider.*;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;


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

    public static final Snippet ReviewProductRequestField = requestFields(
            fieldWithPath("productURL").type(JsonFieldType.STRING).attributes(required())
                    .description("product 를 찾기 위한 URL 값입니다. http 혹은 https 로 시작하는 인터넷 URL 형식이어야 합니다.")
    );

}