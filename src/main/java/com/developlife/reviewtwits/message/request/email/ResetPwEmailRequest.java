package com.developlife.reviewtwits.message.request.email;

import lombok.Builder;

public record ResetPwEmailRequest(String accountPw, String verifyCode) {
    @Builder
    public ResetPwEmailRequest {
    }
}
