package com.developlife.reviewtwits.handler;

import com.developlife.reviewtwits.exception.review.CannotHandleReviewException;
import com.developlife.reviewtwits.exception.review.ReviewNotExistException;
import com.developlife.reviewtwits.message.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static com.developlife.reviewtwits.handler.ExceptionHandlerTool.makeErrorResponse;

/**
 * @author WhalesBob
 * @since 2023-03-18
 */
@RestControllerAdvice
public class ReviewExceptionHandler {

    @ExceptionHandler(CannotHandleReviewException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public List<ErrorResponse> cannotHandleReviewExceptionHandler(CannotHandleReviewException e){
        return makeErrorResponse(e, "X-AUTH-TOKEN");
    }

    @ExceptionHandler(ReviewNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<ErrorResponse> reviewNotExistExceptionHandler(CannotHandleReviewException e){
        return makeErrorResponse(e, "reviewId");
    }
}