package com.developlife.reviewtwits.message.response.product;

import lombok.Builder;

/**
 * @author WhalesBob
 * @since 2023-05-02
 */
public record ProductRegisterResponse(
        long productId,
        String productUrl,
        long projectId) {

    @Builder
    public ProductRegisterResponse{

    }
}