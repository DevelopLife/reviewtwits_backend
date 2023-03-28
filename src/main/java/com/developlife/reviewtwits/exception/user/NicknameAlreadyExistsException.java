package com.developlife.reviewtwits.exception.user;

/**
 * @author ghdic
 * @since 2023/03/28
 */
public class NicknameAlreadyExistsException extends RuntimeException {
    public NicknameAlreadyExistsException(String msg) {
        super(msg);
    }
}
