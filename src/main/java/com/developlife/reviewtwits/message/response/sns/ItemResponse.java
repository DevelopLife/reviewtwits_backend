package com.developlife.reviewtwits.message.response.sns;

import lombok.Builder;

@Builder
public record ItemResponse(
    long itemId,
    String productName,
    String productImageUrl,
    double score,
    String url
) {
}
