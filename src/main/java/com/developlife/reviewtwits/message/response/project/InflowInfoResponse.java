package com.developlife.reviewtwits.message.response.project;

import lombok.Builder;

public record InflowInfoResponse(
        SearchFlowResponse searchFlowResponse,
        DeviceFlow deviceFlow
) {
    @Builder
    public InflowInfoResponse {
    }
}
