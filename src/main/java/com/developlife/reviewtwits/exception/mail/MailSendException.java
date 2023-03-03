package com.developlife.reviewtwits.exception.mail;

/**
 * @author ghdic
 * @since 2023/03/02
 */
public class MailSendException extends RuntimeException{
    public MailSendException(String message) {
        super(message);
    }
}
