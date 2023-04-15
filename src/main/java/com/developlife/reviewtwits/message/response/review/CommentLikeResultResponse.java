package com.developlife.reviewtwits.message.response.review;

import lombok.Builder;

public record CommentLikeResultResponse(
        long commentLikeId,
        long commentId,
        long userId) {

    @Builder
    public CommentLikeResultResponse {
    }
}
