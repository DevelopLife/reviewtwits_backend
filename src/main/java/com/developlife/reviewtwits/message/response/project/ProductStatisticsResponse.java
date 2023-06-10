package com.developlife.reviewtwits.message.response.project;

import lombok.Builder;

public record ProductStatisticsResponse(
        String productName,
        Long visitCount,
        Long reviewCount,
        Long mainAge,
        String mainGender,
        Double averageScore
) {
    @Builder
    public ProductStatisticsResponse {
    }
}
