package com.developlife.reviewtwits.message.response.user;

import lombok.Builder;

/**
 * @author ghdic
 * @since 2023/02/27
 */
@Builder
public record JwtTokenResponse(String accessToken, String tokenType, String provider) {
    public JwtTokenResponse {
    }
}
