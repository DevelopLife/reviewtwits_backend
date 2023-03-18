package com.developlife.reviewtwits.exception.review;

/**
 * @author WhalesBob
 * @since 2023-03-17
 */
public class ReviewNotExistException extends RuntimeException{
    public ReviewNotExistException(String msg){
        super(msg);
    }
}