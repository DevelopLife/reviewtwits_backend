package com.developlife.reviewtwits.exception.sns;

/**
 * @author WhalesBob
 * @since 2023-03-20
 */
public class UnfollowAlreadyDoneException extends AlreadyDoneException{
    public UnfollowAlreadyDoneException(String msg){
        super(msg);
    }
}