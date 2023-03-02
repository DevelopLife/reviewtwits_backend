package com.developlife.reviewtwits.message.request.user;

import com.developlife.reviewtwits.type.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * @author ghdic
 * @since 2023/02/19
 */
@Builder
public record RegisterUserRequest(
        String nickname,
        @Email(message = "이메일 형식이 아닙니다.")
        String accountId,
        @Pattern(message = "비밀번호는 6자리 이상, 영문, 숫자, 특수문자 조합이어야 합니다.",
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{6,}$")
        String accountPw,
        // @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime birthday,
        @NotBlank(message = "휴대폰번호를 입력해주세요")
        String phoneNumber,
        Gender gender,
        @NotBlank(message = "이메일 인증코드를 입력해주세요")
        String authenticationCode // 이메일 인증코드
) {
}