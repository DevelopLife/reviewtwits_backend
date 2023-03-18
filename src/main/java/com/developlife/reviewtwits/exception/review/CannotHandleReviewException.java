package com.developlife.reviewtwits.exception.review;

/**
 * @author WhalesBob
 * @since 2023-03-17
 */
public class CannotHandleReviewException extends RuntimeException{
    public CannotHandleReviewException(String msg){
        super(msg);
    }
}