package com.developlife.reviewtwits;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Snippet;

import static com.developlife.reviewtwits.DocumentFormatProvider.required;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
 //import org.springframework.restdocs.coo
/**
 * @author ghdic
 * @since 2023/03/08
 */
public class CommonDocument {
    public static final Snippet ErrorResponseFields = responseFields(
            fieldWithPath("[].message").type(JsonFieldType.STRING).description("에러메세지"),
            fieldWithPath("[].errorType").type(JsonFieldType.STRING).description("에러타입"),
            fieldWithPath("[].fieldName").type(JsonFieldType.STRING).description("에러난 필드이름")
    );
    public static final Snippet AccessTokenHeader = requestHeaders(
            headerWithName("X-AUTH-TOKEN").attributes(required()).description("access token")
    );

    public static final Snippet OptionalAccessTokenHeader = requestHeaders(
            headerWithName("X-AUTH-TOKEN").description("access token").optional()
    );

    public static final Snippet AuthorizationHeader = requestHeaders(
            headerWithName("Authorization").attributes(required()).description("oauth access token")
    );
}
