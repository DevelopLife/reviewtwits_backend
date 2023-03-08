package com.developlife.reviewtwits.message.request.user;

import com.developlife.reviewtwits.type.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * @author ghdic
 * @since 2023/02/19
 */

public record RegisterUserRequest(
        @Size(min = 2, max = 20, message = "닉네임은 2자리 이상, 20자리 이하로 입력해주세요")
        String nickname,
        @NotBlank(message = "이메일을 입력해주세요")
        @Email(message = "이메일 형식이 아닙니다.")
        String accountId,
        @NotBlank(message = "비밀번호를 입력해주세요")
        @Pattern(message = "비밀번호는 6자리 이상, 영문, 숫자, 특수문자 조합이어야 합니다.",
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{6,}$")
        String accountPw,
        @Pattern(message = "생일 형식이 아닙니다.",
                regexp = "^(19|20)\\d{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$")
        String birthDate,
        @NotBlank(message = "휴대폰번호를 입력해주세요")
        @Pattern(message = "휴대폰번호 형식이 아닙니다.",
                regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$")
        String phoneNumber,
        Gender gender,
        @NotBlank(message = "이메일 인증코드를 입력해주세요")
        String authenticationCode // 이메일 인증코드
) {
        @Builder
        public RegisterUserRequest{

        }
}