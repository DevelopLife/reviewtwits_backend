package com.developlife.reviewtwits.handler;

import com.developlife.reviewtwits.exception.project.ProjectNameDuplicateException;
import com.developlife.reviewtwits.exception.project.ProjectNotFoundException;
import com.developlife.reviewtwits.message.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static com.developlife.reviewtwits.handler.ExceptionHandlerTool.makeErrorResponse;

/**
 * @author ghdic
 * @since 2023/03/10
 */
@RestControllerAdvice
public class ProjectExceptionHandler {
    @ExceptionHandler(ProjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<ErrorResponse> projectIdNotFoundExceptionHandler(ProjectNotFoundException e){
        return makeErrorResponse(e, "projectId");
    }

    @ExceptionHandler(ProjectNameDuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public List<ErrorResponse> projectNameDuplicateExceptionHandler(ProjectNameDuplicateException e){
        return makeErrorResponse(e, "projectName");
    }
}
