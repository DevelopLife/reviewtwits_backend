package com.developlife.reviewtwits.message.request.product;

import com.developlife.reviewtwits.message.annotation.statistics.HttpURL;
import lombok.Builder;

import javax.validation.constraints.Size;

public record ProductRegisterRequest(
        @HttpURL
        String productUrl,
        @HttpURL
        String imageUrl,
        @Size(min = 3, message = "제품명은 3자 이상이어야 합니다.")
        String productName
        ) {
        @Builder
        public ProductRegisterRequest {
        }
}
