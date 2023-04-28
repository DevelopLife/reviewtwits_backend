package com.developlife.reviewtwits.message.response.user;

import lombok.Builder;

/**
 * @author ghdic
 * @since 2023/02/24
 */
public record UserInfoResponse(
        long userId,
        String nickname,
        String accountId,
        String introduceText,
        String profileImageUrl,
        String detailIntroduce,
        int reviewCount,
        int followers,
        int followings,
        boolean isFollowed
) {
    @Builder
    public UserInfoResponse {
    }
}
