package com.developlife.reviewtwits.message.request;

import com.developlife.reviewtwits.message.annotation.statistics.Device;
import com.developlife.reviewtwits.message.annotation.common.HttpURL;
import lombok.Builder;

/**
 * @author WhalesBob
 * @since 2023-04-23
 */

public record StatMessageRequest(
        @HttpURL
        String inflowUrl,
        @HttpURL
        String productUrl,
        @Device
        String device) {

        @Builder
        public StatMessageRequest {
        }
}