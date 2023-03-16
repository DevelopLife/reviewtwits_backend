package com.developlife.reviewtwits.exception.product;

/**
 * @author WhalesBob
 * @since 2023-03-15
 */
public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(String msg){
        super(msg);
    }
}