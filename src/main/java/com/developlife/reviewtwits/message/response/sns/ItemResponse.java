package com.developlife.reviewtwits.message.response.sns;

public record ItemResponse(
    long itemId,
    String productName,
    String productImageUrl,
    double score,
    String url
) {
}
