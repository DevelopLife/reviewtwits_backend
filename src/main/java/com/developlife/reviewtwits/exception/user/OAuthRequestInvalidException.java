package com.developlife.reviewtwits.exception.user;

/**
 * @author ghdic
 * @since 2023/03/02
 */
public class OAuthRequestInvalidException extends RuntimeException{
    public OAuthRequestInvalidException(String message) {
        super(message);
    }
}
