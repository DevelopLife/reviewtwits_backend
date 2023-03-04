package com.developlife.reviewtwits.handler;

import com.developlife.reviewtwits.exception.mail.MailSendException;
import com.developlife.reviewtwits.exception.user.VerifyCodeException;
import com.developlife.reviewtwits.message.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author ghdic
 * @since 2023/03/02
 */
@RestControllerAdvice
public class MailExceptionHandler {
    @ExceptionHandler(MailSendException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse mailSendExceptionHandler(MailSendException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .errorType(e.getClass().getSimpleName())
                .fieldName("accountId")
                .build();
    }

    @ExceptionHandler(VerifyCodeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse verifyCodeExceptionHandler(VerifyCodeException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .errorType(e.getClass().getSimpleName())
                .fieldName("authenticationCode")
                .build();
    }
}
