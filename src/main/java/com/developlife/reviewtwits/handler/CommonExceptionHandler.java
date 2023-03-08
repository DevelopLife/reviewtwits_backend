package com.developlife.reviewtwits.handler;

import com.developlife.reviewtwits.exception.common.DateParseException;
import com.developlife.reviewtwits.exception.user.VerifyCodeException;
import com.developlife.reviewtwits.message.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static com.developlife.reviewtwits.handler.ExceptionHandlerTool.makeErrorResponse;

/**
 * @author ghdic
 * @since 2023/03/08
 */
public class CommonExceptionHandler {
    @ExceptionHandler(DateParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorResponse> dateParseExceptionHandler(DateParseException e) {
        return makeErrorResponse(e, "");
    }
}
