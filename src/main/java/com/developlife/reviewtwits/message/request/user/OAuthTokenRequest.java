package com.developlife.reviewtwits.message.request.user;

import javax.validation.constraints.NotBlank;

/**
 * @author ghdic
 * @since 2023/03/02
 */
public record OAuthTokenRequest(
        @NotBlank(message = "토큰 발급을 위한 인증코드가 필요합니다")
        String code,
        @NotBlank(message = "토큰 제공사에 대한 정보가 필요합니다")
        String provider
) {
}
