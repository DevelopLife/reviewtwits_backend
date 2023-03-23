package com.developlife.reviewtwits.crawling;

import com.developlife.reviewtwits.ApiTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;

/**
 * @author WhalesBob
 * @since 2023-03-22
 */
public class ItemApiTest extends ApiTest {

    @Test
    void crawlingTryTest(){
        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "요청을 받았을 시 해당 상품에 대한 정보를 쿠팡에서 크롤링해서 가져옵니다."
                        , "아이템크롤링요청", ItemApiDocument.itemCrawlingRequestField))
                .config(config().encoderConfig(encoderConfig()
                        .encodeContentTypeAs("x-www-form-urlencoded", ContentType.URLENC)
                        .defaultContentCharset("UTF-8")))
                .param("productName", "페레로로쉐")
                .when()
                .post("/items/register-crawling")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .log().all();

    }
}