package com.developlife.reviewtwits.exception.project;

/**
 * @author WhalesBob
 * @since 2023-05-03
 */
public class ProductUrlInvalidException extends RuntimeException{
    public ProductUrlInvalidException(String msg){
        super(msg);
    }
}