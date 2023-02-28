package com.developlife.reviewtwits.exception.user;

/**
 * @author ghdic
 * @since 2023/02/28
 */
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String msg) {
        super(msg);
    }
}
