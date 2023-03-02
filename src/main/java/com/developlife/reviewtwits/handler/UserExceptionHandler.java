package com.developlife.reviewtwits.handler;

import com.developlife.reviewtwits.exception.user.*;
import com.developlife.reviewtwits.message.response.user.ErrorResponse;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * @author ghdic
 * @since 2023/03/01
 */
@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorResponse> processValidationError(MethodArgumentNotValidException e) {
        List<ErrorResponse> errorResponseList = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    ErrorResponse errorResponse = ErrorResponse.builder()
                            .message(fieldError.getDefaultMessage())
                            .errorType(fieldError.getCode())
                            .fieldName(fieldError.getField())
                            .build();
                    return errorResponse;
                })
                .toList();

        return errorResponseList;
    }

    @ExceptionHandler(AccountIdAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String accountIdAlreadyExistsExceptionHandler(AccountIdAlreadyExistsException e){
        return e.getMessage();
    }

    @ExceptionHandler(AccountPasswordWrongException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String accountPasswordWrongExceptionHandler(AccountPasswordWrongException e){
        return e.getMessage();
    }

    @ExceptionHandler(PasswordVerifyException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String passwordVerifyExceptionHandler(PasswordVerifyException e){
        return e.getMessage();
    }

    @ExceptionHandler(AccountIdNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String accountIdNotFoundExceptionHandler(AccountIdNotFoundException e){
        return e.getMessage();
    }

    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String tokenExpiredExceptionHandler(TokenExpiredException e){
        return e.getMessage();
    }

    @ExceptionHandler(TokenInvalidException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String tokenInvalidExceptionHandler(TokenInvalidException e){
        return e.getMessage();
    }

    @ExceptionHandler(UserIdNotFoundException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String userIdNotFoundExceptionHandler(UserIdNotFoundException e){
        return e.getMessage();
    }
}
