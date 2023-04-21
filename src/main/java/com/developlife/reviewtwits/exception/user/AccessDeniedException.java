package com.developlife.reviewtwits.exception.user;

/**
 * @author ghdic
 * @since 2023/03/21
 */
public class AccessDeniedException extends RuntimeException{
    public AccessDeniedException(String message) {
        super(message);
    }
}
