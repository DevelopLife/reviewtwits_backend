package com.developlife.reviewtwits.message.response.statistics;

import lombok.Builder;

/**
 * @author ghdic
 * @since 2023/06/09
 */
public record SimpleProjectInfoResponse(
        Long monthlyVisitCount,
        Long dailyReviewCount,
        Long pendingReviewCount,
        Long registeredProductCount
) {
    @Builder
    public SimpleProjectInfoResponse {
    }
}
