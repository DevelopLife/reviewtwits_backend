package com.developlife.reviewtwits.exception.user;

/**
 * @author ghdic
 * @since 2023/03/15
 */
public class ProviderNotSupportedException extends RuntimeException {
    public ProviderNotSupportedException(String msg) {
        super(msg);
    }
}
