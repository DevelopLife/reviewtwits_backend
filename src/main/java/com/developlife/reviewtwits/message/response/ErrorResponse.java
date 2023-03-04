package com.developlife.reviewtwits.message.response;

import lombok.Builder;

/**
 * @author ghdic
 * @since 2023/03/02
 */
@Builder
public record ErrorResponse(String message, String errorType, String fieldName) {
    public ErrorResponse {
    }
}
