package com.developlife.reviewtwits.message.response.project;

import lombok.Builder;

public record DeviceFlow(
        Long pc,
        Long mobile
) {
    @Builder
    public DeviceFlow {
    }
}
