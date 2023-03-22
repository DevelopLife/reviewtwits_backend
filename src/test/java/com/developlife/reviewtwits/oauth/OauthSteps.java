package com.developlife.reviewtwits.oauth;

import com.developlife.reviewtwits.message.request.user.RegisterOauthUserRequest;
import com.developlife.reviewtwits.type.Gender;
import com.developlife.reviewtwits.type.JwtProvider;

/**
 * @author ghdic
 * @since 2023/03/15
 */
public class OauthSteps {

    public static RegisterOauthUserRequest 회원가입추가정보생성() {
        return RegisterOauthUserRequest.builder()
            .nickname("nickname")
            .birthDate("1990-01-01")
            .phoneNumber("010-1234-5678")
            .gender(Gender.남자.toString())
            .provider(JwtProvider.GOOGLE.toString())
            .build();
    }
}
