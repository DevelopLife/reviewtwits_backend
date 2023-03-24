package com.developlife.reviewtwits.exception.file;

/**
 * @author WhalesBob
 * @since 2023-03-24
 */
public class InvalidFilenameExtensionException extends RuntimeException{
    public InvalidFilenameExtensionException(String msg){
        super(msg);
    }
}