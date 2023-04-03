package com.developlife.reviewtwits.exception.review;

/**
 * @author WhalesBob
 * @since 2023-04-02
 */
public class CommentNotFoundException extends RuntimeException{
    public CommentNotFoundException(String msg){
        super(msg);
    }
}