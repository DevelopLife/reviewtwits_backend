package com.developlife.reviewtwits.message.response.project;

import lombok.Builder;

public record VisitTotalGraphResponse(String interval,
                                      String range,
                                      long presentVisit,
                                      long previousVisit,
                                      long totalVisit,
                                      VisitInfoResponse visitInfo) {

    @Builder
    public VisitTotalGraphResponse {
    }
}
