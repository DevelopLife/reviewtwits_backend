package com.developlife.reviewtwits.exception.review;

/**
 * @author WhalesBob
 * @since 2023-04-10
 */
public class CommentLikeAlreadyProcessedException extends RuntimeException{
    public CommentLikeAlreadyProcessedException(String msg){
        super(msg);
    }
}