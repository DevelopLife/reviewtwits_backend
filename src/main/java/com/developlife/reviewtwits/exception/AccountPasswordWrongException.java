package com.developlife.reviewtwits.exception;

/**
 * @author ghdic
 * @since 2023/02/20
 */
public class AccountPasswordWrongException extends RuntimeException {
    public AccountPasswordWrongException(String message) {
        super(message);
    }
}
