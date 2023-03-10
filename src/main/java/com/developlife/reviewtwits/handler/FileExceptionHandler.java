package com.developlife.reviewtwits.handler;

import com.developlife.reviewtwits.message.response.ErrorResponse;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static com.developlife.reviewtwits.handler.ExceptionHandlerTool.makeErrorResponse;

@RestControllerAdvice
public class FileExceptionHandler {

    @ExceptionHandler(SizeLimitExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public List<ErrorResponse> sizeLimitExceptionHandler(SizeLimitExceededException e){
        return makeErrorResponse(e, "accountId");
    }
}
