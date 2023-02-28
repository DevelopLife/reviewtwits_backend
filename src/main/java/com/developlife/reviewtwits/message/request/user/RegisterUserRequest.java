package com.developlife.reviewtwits.message.request.user;

import com.developlife.reviewtwits.type.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ghdic
 * @since 2023/02/19
 */
@Builder
public record RegisterUserRequest(
        String nickname,
        String accountId,
        String accountPw,
        LocalDateTime birthday,
        String phoneNumber,
        Gender gender,
        String authenticationCode // 이메일 인증코드
) {
}