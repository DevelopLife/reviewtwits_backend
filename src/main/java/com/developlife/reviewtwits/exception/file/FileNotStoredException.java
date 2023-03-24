package com.developlife.reviewtwits.exception.file;

/**
 * @author WhalesBob
 * @since 2023-03-24
 */
public class FileNotStoredException extends RuntimeException{
    public FileNotStoredException(String msg){
        super(msg);
    }
}