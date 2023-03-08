package com.developlife.reviewtwits.message.request.email;


import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public record FindIdsEmailRequest(
        @NotBlank(message = "휴대번호를 입력해주세요.")
        @Pattern(message = "휴대폰번호 형식이 아닙니다",
        regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$")
        String phoneNumber,
        @NotBlank(message = "생년월일을 입력해주세요.")
        @Pattern(message = "생년월일 형식이 올바르지 않습니다.",
                regexp = "^(19|20)\\d{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$")
        String birthDate
) {
    @Builder
    public FindIdsEmailRequest {
    }
}
