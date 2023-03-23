package com.developlife.reviewtwits.crawling;

import org.springframework.restdocs.snippet.Snippet;

import static com.developlife.reviewtwits.DocumentFormatProvider.required;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

/**
 * @author WhalesBob
 * @since 2023-03-23
 */
public class ItemApiDocument {
    public static final Snippet itemCrawlingRequestField = requestParameters(
            parameterWithName("productName").attributes(required()).description("크롤링 요청하는 상품 이름")
    );
}