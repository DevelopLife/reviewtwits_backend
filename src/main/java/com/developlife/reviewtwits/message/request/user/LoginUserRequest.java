package com.developlife.reviewtwits.message.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author ghdic
 * @since 2023/02/19
 */
@Builder
public record LoginUserRequest(String accountId, String accountPw) {
    public LoginUserRequest {
    }
}
