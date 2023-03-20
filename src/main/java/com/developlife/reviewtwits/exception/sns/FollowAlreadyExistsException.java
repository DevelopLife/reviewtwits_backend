package com.developlife.reviewtwits.exception.sns;
/**
 * @author WhalesBob
 * @since 2023-03-20
 */
public class FollowAlreadyExistsException extends RuntimeException {
    public FollowAlreadyExistsException(String msg){
        super(msg);
    }
}