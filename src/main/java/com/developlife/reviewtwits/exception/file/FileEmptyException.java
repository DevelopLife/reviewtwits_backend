package com.developlife.reviewtwits.exception.file;

/**
 * @author WhalesBob
 * @since 2023-03-29
 */
public class FileEmptyException extends RuntimeException{
    public FileEmptyException(String msg){
        super(msg);
    }
}