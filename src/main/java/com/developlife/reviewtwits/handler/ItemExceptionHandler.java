package com.developlife.reviewtwits.handler;

import com.developlife.reviewtwits.exception.item.CrawlingFailedException;
import com.developlife.reviewtwits.message.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static com.developlife.reviewtwits.handler.ExceptionHandlerTool.makeErrorResponse;

/**
 * @author ghdic
 * @since 2023/04/13
 */
@Slf4j
@RestControllerAdvice
public class ItemExceptionHandler {
    @ExceptionHandler(CrawlingFailedException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public List<ErrorResponse> mailSendExceptionHandler(CrawlingFailedException e) {
        return makeErrorResponse(e, "productName");
    }
}
