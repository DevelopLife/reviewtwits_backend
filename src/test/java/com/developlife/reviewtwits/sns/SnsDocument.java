package com.developlife.reviewtwits.sns;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Snippet;

import static com.developlife.reviewtwits.DocumentFormatProvider.required;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

/**
 * @author WhalesBob
 * @since 2023-03-20
 */
public class SnsDocument {
    public static final Snippet followRequestField = requestFields(
            fieldWithPath("targetUserAccountId").type(JsonFieldType.STRING)
                    .attributes(required()).description("팔로우하려는 계정의 accountId, 이메일 형식이어야 합니다.")
    );

    public static final Snippet followIdField = pathParameters(
            parameterWithName("accountId").attributes(required()).description("유저 계정 아이디")
    );
}