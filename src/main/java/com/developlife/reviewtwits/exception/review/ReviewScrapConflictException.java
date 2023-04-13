package com.developlife.reviewtwits.exception.review;

/**
 * @author WhalesBob
 * @since 2023-04-07
 */
public class ReviewScrapConflictException extends RuntimeException{
    public ReviewScrapConflictException(String msg){
        super(msg);
    }
}