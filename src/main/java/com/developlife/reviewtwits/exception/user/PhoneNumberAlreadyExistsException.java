package com.developlife.reviewtwits.exception.user;

/**
 * @author ghdic
 * @since 2023/03/13
 */
public class PhoneNumberAlreadyExistsException extends RuntimeException {
    public PhoneNumberAlreadyExistsException(String msg) {
        super(msg);
    }
}
