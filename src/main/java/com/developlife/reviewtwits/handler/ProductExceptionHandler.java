package com.developlife.reviewtwits.handler;

import com.developlife.reviewtwits.exception.product.ProductNotRegisteredException;
import com.developlife.reviewtwits.exception.project.ProductUrlInvalidException;
import com.developlife.reviewtwits.message.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static com.developlife.reviewtwits.handler.ExceptionHandlerTool.makeErrorResponse;

/**
 * @author WhalesBob
 * @since 2023-03-15
 */
@RestControllerAdvice
public class ProductExceptionHandler {

    @ExceptionHandler(ProductNotRegisteredException.class)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<ErrorResponse> productNotRegisteredExceptionHandler(ProductNotRegisteredException e){
        return makeErrorResponse(e, "productURL");
    }

    @ExceptionHandler(ProductUrlInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorResponse> productUrlInvalidExceptionHandler(ProductUrlInvalidException e){
        return makeErrorResponse(e, "productURL");
    }
}