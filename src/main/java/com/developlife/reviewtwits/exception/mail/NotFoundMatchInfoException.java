package com.developlife.reviewtwits.exception.mail;

/**
 * @author ghdic
 * @since 2023/03/08
 */
public class NotFoundMatchInfoException extends RuntimeException{
    public NotFoundMatchInfoException(String message) {
        super(message);
    }
}
