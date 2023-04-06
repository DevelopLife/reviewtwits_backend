package com.developlife.reviewtwits.exception.review;

/**
 * @author WhalesBob
 * @since 2023-04-07
 */
public class ReviewScrapNotAddedException extends RuntimeException{
    public ReviewScrapNotAddedException(String msg){
        super(msg);
    }
}