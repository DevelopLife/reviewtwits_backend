package com.developlife.reviewtwits.file;

import com.developlife.reviewtwits.handler.FileExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author WhalesBob
 * @since 2023-02-26
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CheckFiletypeTest extends FileUploadTest{

    @Autowired
    private FileExceptionHandler handler;

    @Test
    @DisplayName("파일 확장자를 검증하는데 있어,  referenceType 에 맞는 확장자 파일 저장 시 202 코드 반환")
    void 파일확장자_검증_일치() throws IOException {
        ResponseEntity<String> response = fileUpload("hello world","forTest",".txt", 12L, "TEST");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("파일 확장자를 검증하는데 있어, referenceType 에 맞지 않는 확장자 파일 저장 시도 시 400 코드 반환")
    void 파일확장자_검증_불일치() throws IOException {

        ResponseEntity<String> response = fileUpload("hello world","forTest",".html", 12L, "TEST");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}