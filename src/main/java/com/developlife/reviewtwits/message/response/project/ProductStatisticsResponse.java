package com.developlife.reviewtwits.message.response.project;

import lombok.Builder;

public record ProductStatisticsResponse(
        String productName,
        Integer visitCount,
        Integer reviewCount,
        Integer mainAge,
        String mainGender,
        Float averageScore
) {
    @Builder
    public ProductStatisticsResponse {
    }
}
