package com.developlife.reviewtwits.exception.user;

/**
 * @author ghdic
 * @since 2023/02/27
 */
public class TokenInvalidException extends RuntimeException {
    public TokenInvalidException(String msg) {
        super(msg);
    }
}
