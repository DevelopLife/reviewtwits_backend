package com.developlife.reviewtwits.exception.project;

/**
 * @author WhalesBob
 * @since 2023-06-15
 */
public class ProjectNameDuplicateException extends RuntimeException {
    public ProjectNameDuplicateException(String message) {
        super(message);
    }
}