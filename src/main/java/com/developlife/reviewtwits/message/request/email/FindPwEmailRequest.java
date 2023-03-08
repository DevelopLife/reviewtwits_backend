package com.developlife.reviewtwits.message.request.email;

import lombok.Builder;

public record FindPwEmailRequest(String accountId, String phoneNumber, String birthDate) {
    @Builder
    public FindPwEmailRequest {
    }
}
