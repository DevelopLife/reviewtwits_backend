package com.developlife.reviewtwits.exception.product;

/**
 * @author WhalesBob
 * @since 2023-05-10
 */
public class ProductAlreadyRegisteredException extends RuntimeException{
    public ProductAlreadyRegisteredException(String msg){
        super(msg);
    }
}