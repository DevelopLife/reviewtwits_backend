package com.developlife.reviewtwits.message.request.user;

import com.developlife.reviewtwits.message.annotation.common.DateFormat;
import com.developlife.reviewtwits.message.annotation.user.Gender;
import com.developlife.reviewtwits.message.annotation.user.Password;
import com.developlife.reviewtwits.message.annotation.user.Phone;
import lombok.Builder;

import javax.validation.constraints.*;

/**
 * @author ghdic
 * @since 2023/02/19
 */

public record RegisterUserRequest(
        @NotBlank(message = "이메일을 입력해주세요")
        @Email(message = "이메일 형식이 아닙니다.")
        String accountId,
        @NotBlank(message = "비밀번호를 입력해주세요")
        @Password
        String accountPw,
        @NotBlank(message = "생년월일을 입력해주세요")
        @DateFormat
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