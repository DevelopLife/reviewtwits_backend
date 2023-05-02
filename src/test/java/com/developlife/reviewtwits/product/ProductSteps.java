package com.developlife.reviewtwits.product;

import com.developlife.reviewtwits.message.request.product.ProductRegisterRequest;

/**
 * @author WhalesBob
 * @since 2023-05-02
 */
public class ProductSteps {

    public static final String exampleProductUrl = "https://localhost:3001/product/123123";
    public static final String wrongProductUrl = "aaa://bbb.ccc";
    public static final long wrongProjectId = 9999L;

    public static ProductRegisterRequest 제품URL등록요청_생성(long projectId) {
        return ProductRegisterRequest.builder()
                .projectId(projectId)
                .productUrl(exampleProductUrl)
                .build();
    }

    public static ProductRegisterRequest 제품URL_프로젝트아이디_이상(){
        return ProductRegisterRequest.builder()
                .productUrl(exampleProductUrl)
                .projectId(wrongProjectId)
                .build();
    }

    public static ProductRegisterRequest 제품URL_URL양식_아님(long projectId){
        return ProductRegisterRequest.builder()
                .productUrl(wrongProductUrl)
                .projectId(projectId)
                .build();
    }

    public static ProductRegisterRequest 제품URL_프로젝트아이디_음수(){
        return ProductRegisterRequest.builder()
                .productUrl(exampleProductUrl)
                .projectId(-1L)
                .build();
    }
}