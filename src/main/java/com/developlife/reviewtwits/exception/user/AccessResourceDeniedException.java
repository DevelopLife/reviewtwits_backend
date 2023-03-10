package com.developlife.reviewtwits.exception.user;

/**
 * @author ghdic
 * @since 2023/03/10
 */
public class AccessResourceDeniedException extends RuntimeException {
    public AccessResourceDeniedException(String msg) {
        super(msg);
    }
}
