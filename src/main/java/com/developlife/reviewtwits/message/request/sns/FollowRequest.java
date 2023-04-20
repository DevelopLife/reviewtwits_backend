package com.developlife.reviewtwits.message.request.sns;

import lombok.Builder;

import javax.validation.constraints.NotBlank;

/**
 * @author WhalesBob
 * @since 2023-03-20
 */

public record FollowRequest(
        @NotBlank
        String targetUserNickname) {

        @Builder
        public FollowRequest{

        }
}