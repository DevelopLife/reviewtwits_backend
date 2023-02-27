package com.developlife.reviewtwits.message.response;

import lombok.Builder;

/**
 * @author ghdic
 * @since 2023/02/27
 */
@Builder
public record JwtTokenResponse(String accessToken, String refreshToken, String tokenType) {
    public JwtTokenResponse {
    }
}
