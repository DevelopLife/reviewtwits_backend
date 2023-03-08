package com.developlife.reviewtwits.handler;

import com.developlife.reviewtwits.exception.mail.MailSendException;
import com.developlife.reviewtwits.exception.mail.NoContentMatchInfoException;
import com.developlife.reviewtwits.exception.mail.NotFoundMatchInfoException;
import com.developlife.reviewtwits.exception.user.VerifyCodeException;
import com.developlife.reviewtwits.message.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static com.developlife.reviewtwits.handler.ExceptionHandlerTool.makeErrorResponse;

/**
 * @author ghdic
 * @since 2023/03/02
 */
@RestControllerAdvice
public class MailExceptionHandler {
    @ExceptionHandler(MailSendException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorResponse> mailSendExceptionHandler(MailSendException e) {
        return makeErrorResponse(e, "accountId");
    }

    @ExceptionHandler(VerifyCodeException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public List<ErrorResponse> verifyCodeExceptionHandler(VerifyCodeException e) {
        return makeErrorResponse(e, "authenticationCode");
    }

    // 204는 에러메세지나 추가정보를 보내지 않음
    @ExceptionHandler(NoContentMatchInfoException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void notFoundMatchInfoExceptionHandler(NoContentMatchInfoException e) {
        // return makeErrorResponse(e, "");
    }

    @ExceptionHandler(NotFoundMatchInfoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorResponse> notFoundMatchInfoExceptionHandler(NotFoundMatchInfoException e) {
         return makeErrorResponse(e, "");
    }
}
