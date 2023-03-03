package com.developlife.reviewtwits.handler;

import com.developlife.reviewtwits.message.response.user.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * @author ghdic
 * @since 2023/03/02
 */
@RestControllerAdvice
public class ValidExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorResponse> processValidationError(MethodArgumentNotValidException e) {
        List<ErrorResponse> errorResponseList = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    ErrorResponse errorResponse = ErrorResponse.builder()
                            .message(fieldError.getDefaultMessage())
                            .errorType(fieldError.getCode())
                            .fieldName(fieldError.getField())
                            .build();
                    return errorResponse;
                })
                .toList();

        return errorResponseList;
    }
}
