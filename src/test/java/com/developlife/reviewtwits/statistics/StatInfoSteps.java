package com.developlife.reviewtwits.statistics;

import com.developlife.reviewtwits.message.request.StatMessageRequest;

/**
 * @author WhalesBob
 * @since 2023-04-23
 */
public class StatInfoSteps {

    public static final String inflowUrl = "https://www.naver.com/";
    public static final String productUrl = "http://www.example.com/123";
    public static final String device = "PC";

    public static StatMessageRequest 통계정보_생성() {
        return StatMessageRequest.builder()
                .inflowUrl(inflowUrl)
                .productUrl(productUrl)
                .device("PC")
                .build();
    }
}