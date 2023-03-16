package com.developlife.reviewtwits.file;

import org.springframework.restdocs.snippet.Snippet;

import static com.developlife.reviewtwits.DocumentFormatProvider.required;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

/**
 * @author WhalesBob
 * @since 2023-03-12
 */
public class FileDownloadDocument {
    public static final Snippet uuidFileName = pathParameters(
            parameterWithName("UUID").attributes(required()).description("UUID 와 확장자로 구성된 파일 이름")
    );
}