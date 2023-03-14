package com.developlife.reviewtwits.message.response.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

/**
 * @author ghdic
 * @since 2023/03/14
 */
// resultcode, message, response
@RequiredArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverUserInfo implements OauthUserInfo {
    private final String resultcode;
    private final String message;
    private final NaverResponse response;

    @Override
    public String sub() {
        return response.id();
    }

    @Override
    public String email() {
        return response.email();
    }
}
