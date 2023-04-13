package com.developlife.reviewtwits.handler;

import com.developlife.reviewtwits.exception.review.*;
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

    @ExceptionHandler(ReviewNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<ErrorResponse> reviewNotFoundExceptionHandler(ReviewNotFoundException e){
        return makeErrorResponse(e, "reviewId");
    }

    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<ErrorResponse> commentNotFoundExceptionHandler(CommentNotFoundException e){
        return makeErrorResponse(e, "comment");
    }

    @ExceptionHandler(ReactionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<ErrorResponse> reactionNotFoundExceptionHandler(ReactionNotFoundException e){
        return makeErrorResponse(e, "reaction");
    }

    @ExceptionHandler(ReviewScrapConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public List<ErrorResponse> reviewScrapConflictExceptionHandler(ReviewScrapConflictException e){
        return makeErrorResponse(e, "");
    }

    @ExceptionHandler(CommentLikeAlreadyProcessedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public List<ErrorResponse> commentLikeAlreadyProcessedExceptionHandler(CommentLikeAlreadyProcessedException e){
        return makeErrorResponse(e,"");
    }
}