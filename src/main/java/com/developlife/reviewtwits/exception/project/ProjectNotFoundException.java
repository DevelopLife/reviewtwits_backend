package com.developlife.reviewtwits.exception.project;

/**
 * @author ghdic
 * @since 2023/03/10
 */
public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(String msg) {
        super(msg);
    }
}
