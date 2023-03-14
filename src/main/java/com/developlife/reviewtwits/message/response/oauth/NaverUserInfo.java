package com.developlife.reviewtwits.message.response.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

/**
 * @author ghdic
 * @since 2023/03/14
 */
// resultcode, message, response
@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverUserInfo(
    String resultcode,
    String message,
    NaverResponse response
) {
    @Builder
    public NaverUserInfo {
    }
}
