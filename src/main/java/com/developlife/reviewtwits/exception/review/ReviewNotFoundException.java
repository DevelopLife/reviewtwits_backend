package com.developlife.reviewtwits.exception.review;

/**
 * @author WhalesBob
 * @since 2023-03-17
 */
public class ReviewNotFoundException extends RuntimeException{
    public ReviewNotFoundException(String msg){
        super(msg);
    }
}