package com.developlife.reviewtwits.message.response;

import lombok.Builder;

/**
 * @author ghdic
 * @since 2023/03/02
 */

public record ErrorResponse(String message, String errorType, String fieldName) {
    @Builder
    public ErrorResponse {
    }
}
