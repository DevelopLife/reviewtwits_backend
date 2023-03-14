package com.developlife.reviewtwits.message.response.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

/**
 * @author ghdic
 * @since 2023/03/14
 */

// 현재 받아와지는 정보 sub, email, email_verified
@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoUserInfo(
    String sub,
    String name,
    String nickname,
    String picture,
    String email,
    String email_verified,
    String gender,
    String birthday,
    Boolean phone_number_verified
) {
    @Builder
    public KakaoUserInfo {
    }
}
