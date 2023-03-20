package com.developlife.reviewtwits.handler;

import com.developlife.reviewtwits.exception.sns.FollowAlreadyExistsException;
import com.developlife.reviewtwits.message.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static com.developlife.reviewtwits.handler.ExceptionHandlerTool.makeErrorResponse;

/**
 * @author WhalesBob
 * @since 2023-03-20
 */
@RestControllerAdvice
public class SnsExceptionHandler {
    @ExceptionHandler(FollowAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public List<ErrorResponse> followAlreadyExistsExceptionHandler(FollowAlreadyExistsException e){
        return makeErrorResponse(e, "targetUserAccountId");
    }
}