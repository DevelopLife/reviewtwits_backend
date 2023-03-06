package com.developlife.reviewtwits.user;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.stereotype.Component;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

/**
 * @author ghdic
 * @since 2023/03/06
 */
public class UserDocsFields {
    public static final Snippet UserInfoRequestField = pathParameters(
            parameterWithName("userId").description("유저아이디")
    );

    public static final Snippet UserInfoResponseField = responseFields(
            fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
            fieldWithPath("accountId").type(JsonFieldType.STRING).description("아이디")
    );
}
