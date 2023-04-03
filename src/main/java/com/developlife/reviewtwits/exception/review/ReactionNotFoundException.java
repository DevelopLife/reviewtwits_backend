package com.developlife.reviewtwits.exception.review;

/**
 * @author WhalesBob
 * @since 2023-04-02
 */
public class ReactionNotFoundException extends RuntimeException{
    public ReactionNotFoundException(String msg){
        super(msg);
    }
}