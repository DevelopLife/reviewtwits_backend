package com.developlife.reviewtwits.exception.product;

/**
 * @author WhalesBob
 * @since 2023-03-15
 */
public class ProductNotRegisteredException extends RuntimeException{
    public ProductNotRegisteredException(String msg){
        super(msg);
    }
}