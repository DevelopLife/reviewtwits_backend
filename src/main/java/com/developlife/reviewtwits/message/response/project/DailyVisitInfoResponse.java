package com.developlife.reviewtwits.message.response.project;

import lombok.Builder;

/**
 * @author WhalesBob
 * @since 2023-04-22
 */
public record DailyVisitInfoResponse(String range,
                                     VisitInfoResponse visitInfo
                                     ) {

    @Builder
    public DailyVisitInfoResponse {
    }
}