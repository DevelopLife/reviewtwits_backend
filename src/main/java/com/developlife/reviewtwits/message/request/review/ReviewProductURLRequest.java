package com.developlife.reviewtwits.message.request.review;

import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author WhalesBob
 * @since 2023-03-15
 */
public record ReviewProductURLRequest(
        @NotBlank(message = "productURL 정보는 비어있을 수 없습니다")
        @Pattern(message = "http 혹은 https 로 시작하는 인터넷 페이지 URL 형식이 아닙니다.",
                regexp = "^(https?://)[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+/[a-zA-Z0-9-_/.?=]*")
        String productURL) {

    @Builder
    public ReviewProductURLRequest{

    }
}