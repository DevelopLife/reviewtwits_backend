package com.developlife.reviewtwits.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author ghdic
 * @since 2023.02.19
 */
public class AccountIdNotFoundException extends RuntimeException {
    public AccountIdNotFoundException(String message) {
        super(message);
    }
}
