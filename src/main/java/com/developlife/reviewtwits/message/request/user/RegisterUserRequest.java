package com.developlife.reviewtwits.message.request.user;

import com.developlife.reviewtwits.message.annotation.user.Birthday;
import com.developlife.reviewtwits.message.annotation.user.Gender;
import com.developlife.reviewtwits.message.annotation.user.Password;
import com.developlife.reviewtwits.message.annotation.user.Phone;
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
        @Password
        String accountPw,
        @NotBlank(message = "생년월일을 입력해주세요")
        @Birthday
        String birthDate,
        @NotBlank(message = "휴대폰번호를 입력해주세요")
        @Phone
        String phoneNumber,
        @Gender
        String gender,
        @NotBlank(message = "이메일 인증코드를 입력해주세요")
        String verifyCode // 이메일 인증코드
) {
        @Builder
        public RegisterUserRequest{

        }
}