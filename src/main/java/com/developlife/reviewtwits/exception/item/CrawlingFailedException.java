package com.developlife.reviewtwits.exception.item;

/**
 * @author ghdic
 * @since 2023/04/13
 */
public class CrawlingFailedException extends RuntimeException {
    public CrawlingFailedException(String msg) {
        super(msg);
    }
}
