package com.developlife.reviewtwits.message.request.user;

import com.developlife.reviewtwits.message.annotation.user.Password;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * @author ghdic
 * @since 2023/02/19
 */

public record LoginUserRequest(
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "이메일 형식이 아닙니다.")
    String accountId,
    @NotBlank(message = "비밀번호를 입력해주세요")
    String accountPw) {
    @Builder
    public LoginUserRequest {
    }
}
