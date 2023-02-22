package com.developlife.reviewtwits.exceptions;

import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FileExceptionHandler {

    @ExceptionHandler(SizeLimitExceededException.class)
    public ResponseEntity<String> sizeLimitExceptionHandler(){
        return new ResponseEntity<>(HttpStatus.PAYLOAD_TOO_LARGE);
    }
}
