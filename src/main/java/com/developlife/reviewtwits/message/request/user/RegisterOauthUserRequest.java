package com.developlife.reviewtwits.message.request.user;

import com.developlife.reviewtwits.message.annotation.oauth.JwtProvider;
import com.developlife.reviewtwits.message.annotation.common.DateFormat;
import com.developlife.reviewtwits.message.annotation.user.Gender;
import com.developlife.reviewtwits.message.annotation.user.Phone;
import lombok.Builder;

import javax.validation.constraints.NotBlank;

/**
 * @author ghdic
 * @since 2023/03/15
 */
public record RegisterOauthUserRequest(
    @JwtProvider
    String provider,
    @NotBlank(message = "생년월일을 입력해주세요")
    @DateFormat
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
