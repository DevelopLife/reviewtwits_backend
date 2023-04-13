package com.developlife.reviewtwits.crawling;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.repository.ItemDetailRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author WhalesBob
 * @since 2023-03-22
 */
public class ItemApiTest extends ApiTest {

    @Autowired
    private ItemDetailRepository itemDetailRepository;

    @Test
    void 상품정보검색힌트_힌트_200() {
        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        "쿠팡의 상품검색힌트기능을 가져와 상품을 선택할 수 있게 합니다. 선택시 상품정보 크롤링 요청을 해주셔야합니다",
                        "상품정보검색힌트",
                        ItemApiDocument.itemProductNameRequest))
                .config(config().encoderConfig(encoderConfig()
                        .encodeContentTypeAs("x-www-form-urlencoded", ContentType.URLENC)
                        .defaultContentCharset("UTF-8")))
                .param("productName", "페레로로쉐")
                .when()
                .get("/items/search")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all();
    }
/*
    @Test
    void 상품정보크롤링_상품정보_200(){
        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        "요청을 받았을 시 해당 상품에 대한 정보를 쿠팡에서 크롤링해서 가져옵니다."
                        , "아이템크롤링요청", ItemApiDocument.itemProductNameRequest
//                        ItemApiDocument.relateProductResponseField
                ))
                .config(config().encoderConfig(encoderConfig()
                        .encodeContentTypeAs("x-www-form-urlencoded", ContentType.URLENC)
                        .defaultContentCharset("UTF-8")))
                .param("productName", "페레로로쉐")
                .when()
                .post("/items/request-crawling")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all();
    }
*/
}