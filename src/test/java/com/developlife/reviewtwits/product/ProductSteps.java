package com.developlife.reviewtwits.product;

import com.developlife.reviewtwits.message.request.product.ProductRegisterRequest;

/**
 * @author WhalesBob
 * @since 2023-05-02
 */
public class ProductSteps {

    public static final String exampleProductName = "제품명";
    public static final String exampleProductUrl = "https://localhost:3001/product/123123";
    public static final String wrongProductUrl = "aaa://bbb.ccc";

    public static final String exampleImageUrl = "https://avatars.githubusercontent.com/u/96509257?s=400&u=a85a0f83480a7df2cd8c267c9f54e8a9e63ffe9a&v=4";

    public static ProductRegisterRequest 제품URL등록요청_생성() {
        return ProductRegisterRequest.builder()
                .productName(exampleProductName)
                .productUrl(exampleProductUrl)
                .imageUrl(exampleImageUrl)
                .build();
    }

    public static ProductRegisterRequest 제품URL_URL양식_아님(){
        return ProductRegisterRequest.builder()
                .productUrl(wrongProductUrl)
                .productName(exampleProductName)
                .imageUrl(exampleImageUrl)
                .build();
    }

    public static ProductRegisterRequest 제품URL_제품이름_누락() {
        return ProductRegisterRequest.builder()
                .productUrl(wrongProductUrl)
                .imageUrl(exampleImageUrl)
                .build();
    }
}