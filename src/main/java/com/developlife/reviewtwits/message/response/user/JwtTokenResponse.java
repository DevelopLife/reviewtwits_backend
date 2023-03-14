package com.developlife.reviewtwits.message.response.user;

import lombok.Builder;

/**
 * @author ghdic
 * @since 2023/02/27
 */

public record JwtTokenResponse(String refreshToken, String accessToken, String tokenType, String provider) {
    @Builder
    public JwtTokenResponse {
    }
}
