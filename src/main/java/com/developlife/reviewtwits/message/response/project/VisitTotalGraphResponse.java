package com.developlife.reviewtwits.message.response.project;

import lombok.Builder;

import java.util.List;

public record VisitTotalGraphResponse(String interval,
                                      String range,
                                      long todayVisit,
                                      long yesterdayVisit,
                                      long totalVisit,
                                      List<VisitInfoResponse> visitInfo) {

    @Builder
    public VisitTotalGraphResponse {
    }
}
