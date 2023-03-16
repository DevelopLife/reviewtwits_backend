package com.developlife.reviewtwits.handler;

import com.developlife.reviewtwits.message.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * @author ghdic
 * @since 2023/03/02
 */
@RestControllerAdvice
public class ValidExceptionHandler {
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorResponse> processValidationError(BindException e) {
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

    @ExceptionHandler(DateTimeParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse dateTimeParseExceptionHandler(DateTimeParseException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("날짜 형식이 올바르지 않습니다. [yyyy-MM-ddTHH:mm:ss] 포맷을 지켜주세요")
                .errorType(e.getClass().getSimpleName())
                .fieldName("")
                .build();
        return errorResponse;
    }
}
