package com.developlife.reviewtwits.exception.user;

/**
 * @author ghdic
 * @since 2023/03/02
 */
public class UserIdNotFoundException extends RuntimeException{
    public UserIdNotFoundException(String message) {
        super(message);
    }
}
