package com.developlife.reviewtwits.message.response.project;

import lombok.Builder;

public record RecentVisitInfoResponse(int todayVisit,
                                      int yesterdayVisit,
                                      int totalVisit) {
    @Builder
    public RecentVisitInfoResponse {
    }
}
