package com.developlife.reviewtwits.message.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author ghdic
 * @since 2023/02/19
 */
public record LoginUserRequest(String accountId, String accountPw) {
    public LoginUserRequest {
    }
}
