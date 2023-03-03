package com.developlife.reviewtwits.handler;

import com.developlife.reviewtwits.exception.mail.MailSendException;
import com.developlife.reviewtwits.exception.user.VerifyCodeException;
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
public class MailExceptionHandler {
    @ExceptionHandler(MailSendException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String mailSendExceptionHandler(MailSendException e) {
        return e.getMessage();
    }

    @ExceptionHandler(VerifyCodeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String verifyCodeExceptionHandler(VerifyCodeException e) {
        return e.getMessage();
    }
}
