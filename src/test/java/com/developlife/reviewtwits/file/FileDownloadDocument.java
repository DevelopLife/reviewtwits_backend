package com.developlife.reviewtwits.file;

import org.springframework.restdocs.snippet.Snippet;

import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

/**
 * @author WhalesBob
 * @since 2023-03-12
 */
public class FileDownloadDocument {
    public static final Snippet uuidFileName = pathParameters(
            parameterWithName("UUID").description("file name made of UUID")
    );
}