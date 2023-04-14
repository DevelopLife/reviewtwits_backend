package com.developlife.reviewtwits.message.response.review;

import lombok.Builder;

/**
 * @author WhalesBob
 * @since 2023-04-14
 */
public record ReviewScrapResultResponse(
        long reviewScrapId,
        long reviewId,
        long userId
) {

    @Builder
    public ReviewScrapResultResponse {
    }
}