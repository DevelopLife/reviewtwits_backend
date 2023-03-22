package com.developlife.reviewtwits.exception.sns;


/**
 * @author WhalesBob
 * @since 2023-03-20
 */
public class AlreadyDoneException extends RuntimeException{
    public AlreadyDoneException(String msg){
        super(msg);
    }
}