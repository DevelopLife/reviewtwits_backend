package com.developlife.reviewtwits.message.response.project;

import lombok.Builder;

public record InflowInfoResponse(
        SearchFlow searchFlow,
        DeviceFlow deviceFlow
) {
    @Builder
    public InflowInfoResponse {
    }
}
