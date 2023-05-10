package com.developlife.reviewtwits.message.request.review;

import com.developlife.reviewtwits.message.annotation.common.HttpURL;
import lombok.Builder;

import javax.validation.constraints.NotBlank;

/**
 * @author WhalesBob
 * @since 2023-03-15
 */
public record ReviewProductURLRequest(
        @NotBlank(message = "productURL 정보는 비어있을 수 없습니다")
        @HttpURL
        String productURL) {

    @Builder
    public ReviewProductURLRequest{

    }
}