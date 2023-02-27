package com.developlife.reviewtwits.exception.user;

/**
 * @author ghdic
 * @since 2023/02/27
 */
public class RefreshTokenInvalidException extends RuntimeException {
    public RefreshTokenInvalidException(String msg) {
        super(msg);
    }
}
