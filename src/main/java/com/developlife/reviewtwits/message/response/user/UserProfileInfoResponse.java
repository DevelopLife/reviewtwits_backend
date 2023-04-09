package com.developlife.reviewtwits.message.response.user;

import lombok.Builder;

/**
 * @author WhalesBob
 * @since 2023-04-09
 */
public record UserProfileInfoResponse(
        UserInfoResponse userInfo,
        int reviewCount,
        int followers,
        int followings
) {

    @Builder
    public UserProfileInfoResponse{

    }
}