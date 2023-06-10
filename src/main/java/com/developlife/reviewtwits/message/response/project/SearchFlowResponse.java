package com.developlife.reviewtwits.message.response.project;

import lombok.Builder;

public record SearchFlowResponse(
        Long total,
        Long naver,
        Long daum,
        Long google,
        Long zoom,
        Long bing,
        Long yahoo,
        Long etc
) {
    @Builder
    public SearchFlowResponse {
    }
}
