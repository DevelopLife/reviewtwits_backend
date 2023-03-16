package com.developlife.reviewtwits.message.response.user;

import com.developlife.reviewtwits.type.JwtProvider;
import lombok.Builder;

/**
 * @author ghdic
 * @since 2023/02/27
 */

public record JwtTokenResponse(String accessToken, String tokenType, JwtProvider provider) {
    @Builder
    public JwtTokenResponse {
    }
}
