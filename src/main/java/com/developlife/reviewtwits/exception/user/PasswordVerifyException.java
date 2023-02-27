package com.developlife.reviewtwits.exception.user;

/**
 * @author ghdic
 * @since 2023/02/24
 */
public class PasswordVerifyException extends RuntimeException {
    public PasswordVerifyException(String msg) {
        super(msg);
    }
}
