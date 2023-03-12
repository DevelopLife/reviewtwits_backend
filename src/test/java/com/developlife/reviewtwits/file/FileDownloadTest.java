package com.developlife.reviewtwits.file;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.CommonDocument;
import com.google.common.net.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;
import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;


/**
 * @author WhalesBob
 * @since 2023-03-12
 */
public class FileDownloadTest extends ApiTest {

    @Autowired
    private FileDownloadSteps downloadSteps;

    private String uuidFilename;

    @BeforeEach
    void settings() throws IOException {
        uuidFilename = downloadSteps.textFileUpload("example","example",".txt",12L,"TEST");
        // 임의의 파일과 이미지 하나를 저장해 두고, 없는 거 부르면 실패/있는거 부르면 성공

    }


    @Test
    void 파일다운로드요청_성공_200(){

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "UUID 로 저장된 이름으로 파일을 찾아, 다운로드할 수 있는 태그를 만듭니다.","파일다운로드요청", FileDownloadDocument.uuidFileName))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("UUID",uuidFilename)
        .when()
                .get("/request-download-files/{UUID}")
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .header(HttpHeaders.CONTENT_DISPOSITION,notNullValue())
                .header(HttpHeaders.CONTENT_TYPE,notNullValue())
                .body(notNullValue())
                .log().all().extract();
    }

    @Test
    void 파일다운로드요청_실패_404(){
        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("UUID","123123.txt")
        .when()
                .get("/request-download-files/{UUID}")
        .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .header(HttpHeaders.CONTENT_TYPE,"application/json")
                .body("find{it.errorType == 'FileNotFoundException' " +
                        "&& it.fieldName == 'fileName' " + "&& it.message == '해당 파일이 존재하지 않습니다.'}", notNullValue())
                .log().all().extract();
    }
}