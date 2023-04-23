package com.developlife.reviewtwits.message.response.project;

import lombok.Builder;

import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-04-22
 */
public record VisitInfoResponse(List<String> timeStamp,
                                List<Integer> visitCount,
                                List<Integer> previousCompare
                                ) {
    @Builder
    public VisitInfoResponse {
    }
}