package com.developlife.reviewtwits.message.request.user;

import com.developlife.reviewtwits.message.annotation.oauth.JwtProvider;
import com.developlife.reviewtwits.message.annotation.user.Gender;
import com.developlife.reviewtwits.message.annotation.user.Phone;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author ghdic
 * @since 2023/03/15
 */
public record RegisterOauthUserRequest(
    @JwtProvider
    String provider,
    @Pattern(message = "생일 형식이 아닙니다.",
        regexp = "^(19|20)\\d{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$")
    String birthDate,
    @NotBlank(message = "휴대폰번호를 입력해주세요")
    @Phone
    String phoneNumber,
    @Gender
    String gender
) {
    @Builder
    public RegisterOauthUserRequest {

    }
}
