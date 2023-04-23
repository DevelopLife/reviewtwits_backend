package com.developlife.reviewtwits.statistics;

import com.developlife.reviewtwits.message.request.StatMessageRequest;

/**
 * @author WhalesBob
 * @since 2023-04-23
 */
public class StatInfoSteps {

    public static final String inflowUrl = "https://www.naver.com/";
    public static final String productUrl = "http://www.example.com/123";
    public static final String wrongUrl = "asdf";
    public static final String notRegisteredProductUrl = "http://www.example.com/456";
    public static final String device = "PC";
    public static final String wrongDevice = "TABLET";

    public static StatMessageRequest 통계정보_생성() {
        return StatMessageRequest.builder()
                .inflowUrl(inflowUrl)
                .productUrl(productUrl)
                .device(device)
                .build();
    }

    public static StatMessageRequest 통계정보_생성_productUrl_미포함(){
        return StatMessageRequest.builder()
                .inflowUrl(inflowUrl)
                .device(device)
                .build();
    }

    public static StatMessageRequest 통계정보_생성_URL_형식아님(){
        return StatMessageRequest.builder()
                .inflowUrl(wrongUrl)
                .productUrl(productUrl)
                .device(device)
                .build();
    }

    public static StatMessageRequest 통계정보_생성_device_형식아님(){
        return StatMessageRequest.builder()
                .inflowUrl(inflowUrl)
                .productUrl(productUrl)
                .device(wrongDevice)
                .build();
    }

    public static StatMessageRequest 통계정보_생성_device_미포함(){
        return StatMessageRequest.builder()
                .inflowUrl(inflowUrl)
                .productUrl(productUrl)
                .build();
    }

    public static StatMessageRequest 통계정보_생성_등록되지않은_상품_URL() {
        return StatMessageRequest.builder()
                .inflowUrl(inflowUrl)
                .productUrl(notRegisteredProductUrl)
                .device(device)
                .build();
    }
}