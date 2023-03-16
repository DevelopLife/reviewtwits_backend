package com.developlife.reviewtwits.message.request.user;

import com.developlife.reviewtwits.type.Gender;
import com.developlife.reviewtwits.type.JwtProvider;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author ghdic
 * @since 2023/03/15
 */
public record RegisterOauthUserRequest(
    JwtProvider provider,
    @Size(min = 2, max = 20, message = "닉네임은 2자리 이상, 20자리 이하로 입력해주세요")
    String nickname,
    @Pattern(message = "생일 형식이 아닙니다.",
        regexp = "^(19|20)\\d{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$")
    String birthDate,
    @NotBlank(message = "휴대폰번호를 입력해주세요")
    @Pattern(message = "휴대폰번호 형식이 아닙니다.",
        regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$")
    String phoneNumber,
    Gender gender
) {
    @Builder
    public RegisterOauthUserRequest {

    }
}
