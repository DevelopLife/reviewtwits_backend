package com.developlife.reviewtwits.message.response.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

/**
 * @author ghdic
 * @since 2023/03/14
 */
// 현재 받는 정보: sub, name, given_name, family_name, picture, email, email_verified, locale
@RequiredArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleUserInfo implements OauthUserInfo {
    private final String sub;
    private final String name;
    private final String given_name;
    private final String family_name;
    private final String picture;
    private final String email;
    private final Boolean email_verified;
    private final String locale;

    @Override
    public String sub() {
        return sub;
    }

    @Override
    public String email() {
        return email;
    }
}
