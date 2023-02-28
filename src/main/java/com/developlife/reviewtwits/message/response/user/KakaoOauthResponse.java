package com.developlife.reviewtwits.message.response.user;

import lombok.Builder;
import lombok.Data;

/**
 * @author ghdic
 * @since 2023/02/22
 */
@Builder
public record KakaoOauthResponse(
        String token_type,
        String access_token,
        String id_token,
        int expires_in,
        String refresh_token,
        int refresh_token_expires_in,
        String scope
) {
}
