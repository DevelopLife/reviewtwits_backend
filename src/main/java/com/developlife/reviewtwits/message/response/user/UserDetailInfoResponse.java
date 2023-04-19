package com.developlife.reviewtwits.message.response.user;

import com.developlife.reviewtwits.type.Gender;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * @author ghdic
 * @since 2023/02/25
 */
public record UserDetailInfoResponse(
        String nickname,
        String accountId,
        String birthDate,
        String phoneNumber,
        Gender gender,
        String provider,
        String uuid,
        String profileImageUrl,
        String introduceText
) {
    @Builder
    public UserDetailInfoResponse {
    }
}
