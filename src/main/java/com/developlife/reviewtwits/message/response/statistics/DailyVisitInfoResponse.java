package com.developlife.reviewtwits.message.response.statistics;

import lombok.Builder;

import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-04-22
 */
public record DailyVisitInfoResponse(String range,
                                     List<VisitInfoResponse> visitInfo
                                     ) {

    @Builder
    public DailyVisitInfoResponse {
    }
}