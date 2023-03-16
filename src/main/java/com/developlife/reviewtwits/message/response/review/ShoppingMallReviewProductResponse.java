package com.developlife.reviewtwits.message.response.review;

import lombok.Builder;

/**
 * @author WhalesBob
 * @since 2023-03-15
 */
public record ShoppingMallReviewProductResponse(double averageStarScore, int totalReviewCount, int recentReviewCount, double[] starScoreArray) {

    @Builder
    public ShoppingMallReviewProductResponse{

    }
}