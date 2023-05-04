package com.developlife.reviewtwits.product;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Snippet;

import static com.developlife.reviewtwits.DocumentFormatProvider.required;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

/**
 * @author WhalesBob
 * @since 2023-05-02
 */
public class ProductDocument {

    public static final Snippet ProductUrlRegisterRequestFields = requestFields(
            fieldWithPath("productUrl").type(JsonFieldType.STRING).attributes(required()).description("상품 URL"),
            fieldWithPath("productName").type(JsonFieldType.STRING).attributes(required()).description("제품명"),
            fieldWithPath("imageUrl").type(JsonFieldType.STRING).attributes(required()).description("이미지 URL")
    );
    public static Snippet ProductRegisterResponseFields = responseFields(
            fieldWithPath("productId").type(JsonFieldType.NUMBER).attributes(required()).description("상품 아이디"),
            fieldWithPath("projectId").type(JsonFieldType.NUMBER).attributes(required()).description("프로젝트 아이디"),
            fieldWithPath("productUrl").type(JsonFieldType.STRING).attributes(required()).description("상품 URL"),
            fieldWithPath("productName").type(JsonFieldType.STRING).attributes(required()).description("제품명"),
            fieldWithPath("imageUrl").type(JsonFieldType.STRING).attributes(required()).description("이미지 URL")
    );
}