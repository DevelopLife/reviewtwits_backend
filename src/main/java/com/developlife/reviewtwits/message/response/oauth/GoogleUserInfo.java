package com.developlife.reviewtwits.message.response.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

/**
 * @author ghdic
 * @since 2023/03/14
 */
// 현재 받는 정보: sub, name, given_name, family_name, picture, email, email_verified, locale
@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleUserInfo(
    String sub,
    String name,
    String given_name,
    String family_name,
    String picture,
    String email,
    Boolean email_verified,
    String locale
) {
    @Builder
    public GoogleUserInfo {
    }
}
