package com.developlife.reviewtwits.handler;

import com.developlife.reviewtwits.exception.user.*;
import com.developlife.reviewtwits.message.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author ghdic
 * @since 2023/03/01
 */
@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(AccountIdAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse accountIdAlreadyExistsExceptionHandler(AccountIdAlreadyExistsException e){
        return ErrorResponse.builder()
                .message(e.getMessage())
                .errorType(e.getClass().getSimpleName())
                .fieldName("accountId")
                .build();
    }

    @ExceptionHandler(AccountPasswordWrongException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse accountPasswordWrongExceptionHandler(AccountPasswordWrongException e){
        return ErrorResponse.builder()
                .message(e.getMessage())
                .errorType(e.getClass().getSimpleName())
                .fieldName("accountPw")
                .build();
    }

    @ExceptionHandler(PasswordVerifyException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse passwordVerifyExceptionHandler(PasswordVerifyException e){
        return ErrorResponse.builder()
                .message(e.getMessage())
                .errorType(e.getClass().getSimpleName())
                .fieldName("accountPw")
                .build();
    }

    @ExceptionHandler(AccountIdNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse accountIdNotFoundExceptionHandler(AccountIdNotFoundException e){
        return ErrorResponse.builder()
                .message(e.getMessage())
                .errorType(e.getClass().getSimpleName())
                .fieldName("accountId")
                .build();
    }

    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse tokenExpiredExceptionHandler(TokenExpiredException e){
        return ErrorResponse.builder()
                .message(e.getMessage())
                .errorType(e.getClass().getSimpleName())
                .fieldName("")
                .build();
    }

    @ExceptionHandler(TokenInvalidException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse tokenInvalidExceptionHandler(TokenInvalidException e){
        return ErrorResponse.builder()
                .message(e.getMessage())
                .errorType(e.getClass().getSimpleName())
                .fieldName("")
                .build();
    }

    @ExceptionHandler(UserIdNotFoundException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ErrorResponse userIdNotFoundExceptionHandler(UserIdNotFoundException e){
        return ErrorResponse.builder()
                .message(e.getMessage())
                .errorType(e.getClass().getSimpleName())
                .fieldName("userId")
                .build();
    }
}
