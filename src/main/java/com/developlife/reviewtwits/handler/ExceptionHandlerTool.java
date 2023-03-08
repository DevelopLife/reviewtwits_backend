package com.developlife.reviewtwits.handler;

import com.developlife.reviewtwits.message.response.ErrorResponse;

import java.util.List;

/**
 * @author ghdic
 * @since 2023/03/08
 */
public class ExceptionHandlerTool {
    public static List<ErrorResponse> makeErrorResponse(Exception e, String fieldName) {
        return List.of(ErrorResponse.builder()
                .message(e.getMessage())
                .errorType(e.getClass().getSimpleName())
                .fieldName(fieldName)
                .build());
    }
}
