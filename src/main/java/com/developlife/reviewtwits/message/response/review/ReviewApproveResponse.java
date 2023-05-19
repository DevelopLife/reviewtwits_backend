package com.developlife.reviewtwits.message.response.review;

import lombok.Builder;

/**
 * @author WhalesBob
 * @since 2023-05-19
 */
public record ReviewApproveResponse(Long reviewId,
                                    String status) {
    @Builder
    public ReviewApproveResponse {
    }
}