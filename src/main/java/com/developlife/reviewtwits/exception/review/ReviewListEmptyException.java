package com.developlife.reviewtwits.exception.review;

/**
 * @author WhalesBob
 * @since 2023-04-26
 */
public class ReviewListEmptyException extends RuntimeException{
    public ReviewListEmptyException(String msg){
        super(msg);
    }
}