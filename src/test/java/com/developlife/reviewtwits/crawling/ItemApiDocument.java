package com.developlife.reviewtwits.crawling;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Snippet;

import static com.developlife.reviewtwits.DocumentFormatProvider.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

/**
 * @author WhalesBob
 * @since 2023-03-23
 */
public class ItemApiDocument {
    public static final Snippet itemProductNameRequest = requestParameters(
            parameterWithName("productName").attributes(required()).description("크롤링 요청하는 상품 이름")
    );
    public static final Snippet relateProductResponseField = responseFields(
            fieldWithPath("productId").type(JsonFieldType.NUMBER).description("상품아이디"),
            fieldWithPath("productUrl").type(JsonFieldType.STRING).description("상품URL"),
            fieldWithPath("name").type(JsonFieldType.STRING).description("상품이름"),
            fieldWithPath("price").type(JsonFieldType.NUMBER).description("상품가격"),
            fieldWithPath("imagePath").type(JsonFieldType.STRING).description("상품 이미지 경로"),
            fieldWithPath("fileName").type(JsonFieldType.STRING).description("이미지 이름")
    );
}