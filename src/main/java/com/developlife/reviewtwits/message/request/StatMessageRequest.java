package com.developlife.reviewtwits.message.request;

import com.developlife.reviewtwits.message.annotation.statistics.Device;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

/**
 * @author WhalesBob
 * @since 2023-04-23
 */

public record StatMessageRequest(
        @URL(message = "URL 형식이 아닙니다.")
        String inflowUrl,
        @URL(message = "URL 형식이 아닙니다.")
        String productUrl,
        @Device
        String device) {

        @Builder
        public StatMessageRequest {
        }
}