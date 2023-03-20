package com.developlife.reviewtwits.message.request.sns;

import lombok.Builder;

import javax.validation.constraints.Email;

/**
 * @author WhalesBob
 * @since 2023-03-20
 */

public record FollowRequest(
        @Email(message = "이메일 형식이 아닙니다.")
        String targetUserAccountId) {

        @Builder
        public FollowRequest{

        }
}