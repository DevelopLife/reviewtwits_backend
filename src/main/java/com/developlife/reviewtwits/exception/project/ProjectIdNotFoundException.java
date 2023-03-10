package com.developlife.reviewtwits.exception.project;

/**
 * @author ghdic
 * @since 2023/03/10
 */
public class ProjectIdNotFoundException extends RuntimeException {
    public ProjectIdNotFoundException(String msg) {
        super(msg);
    }
}
