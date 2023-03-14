package com.developlife.reviewtwits.message.response.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

/**
 * @author ghdic
 * @since 2023/03/14
 */

// 현재 받아와지는 정보 sub, email, email_verified
@RequiredArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfo implements OauthUserInfo {
    private final String sub;
    private final String name;
    private final String nickname;
    private final String picture;
    private final String email;
    private final String email_verified;
    private final String gender;
    private final String birthday;
    private final Boolean phone_number_verified;

    @Override
    public String sub() {
        return sub;
    }

    @Override
    public String email() {
        return email;
    }
}
