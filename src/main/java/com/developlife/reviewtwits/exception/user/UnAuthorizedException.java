package com.developlife.reviewtwits.exception.user;

/**
 * @author ghdic
 * @since 2023/03/21
 */
public class UnAuthorizedException extends RuntimeException{
    public UnAuthorizedException(String message) {
        super(message);
    }
}
