package com.developlife.reviewtwits.handler;

import com.developlife.reviewtwits.exception.user.*;
import com.developlife.reviewtwits.message.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static com.developlife.reviewtwits.handler.ExceptionHandlerTool.makeErrorResponse;

/**
 * @author ghdic
 * @since 2023/03/01
 */
@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(AccountIdAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorResponse> accountIdAlreadyExistsExceptionHandler(AccountIdAlreadyExistsException e){
        return makeErrorResponse(e, "accountId");
    }

    @ExceptionHandler(AccountPasswordWrongException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public List<ErrorResponse> accountPasswordWrongExceptionHandler(AccountPasswordWrongException e){
        return makeErrorResponse(e, "accountPw");
    }

    @ExceptionHandler(PasswordVerifyException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public List<ErrorResponse> passwordVerifyExceptionHandler(PasswordVerifyException e){
        return makeErrorResponse(e, "accountPw");
    }

    @ExceptionHandler(AccountIdNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public List<ErrorResponse> accountIdNotFoundExceptionHandler(AccountIdNotFoundException e){
        return makeErrorResponse(e, "accountId");
    }

    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public List<ErrorResponse> tokenExpiredExceptionHandler(TokenExpiredException e){
        return makeErrorResponse(e, "");
    }

    @ExceptionHandler(TokenInvalidException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public List<ErrorResponse> tokenInvalidExceptionHandler(TokenInvalidException e){
        return makeErrorResponse(e, "");
    }

    @ExceptionHandler(UserIdNotFoundException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public List<ErrorResponse> userIdNotFoundExceptionHandler(UserIdNotFoundException e){
        return makeErrorResponse(e, "userId");
    }

    @ExceptionHandler(AccessResourceDeniedException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public List<ErrorResponse> accessResourceDeniedExceptionHandler(AccessResourceDeniedException e){
        return makeErrorResponse(e, "");
    }

    @ExceptionHandler(PhoneNumberAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorResponse> phoneNumberAlreadyExistsExceptionHandler(PhoneNumberAlreadyExistsException e){
        return makeErrorResponse(e, "phoneNumber");
    }

    @ExceptionHandler(RegisterDataNeedException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public List<ErrorResponse> registerDataNeedExceptionHandler(RegisterDataNeedException e){
        return makeErrorResponse(e, "");
    }

    @ExceptionHandler(ProviderNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorResponse> providerNotSupportedExceptionHandler(ProviderNotSupportedException e){
        return makeErrorResponse(e, "provider");
    }
}
