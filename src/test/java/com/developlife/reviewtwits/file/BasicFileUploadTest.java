package com.developlife.reviewtwits.file;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BasicFileUploadTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("파일 업로드 시, 같은 내용으로 파일이 존재하는지 확안하기.")
    void checkFileUpload_existInDirectory() throws IOException {

        Path path = Files.createTempFile("test",".txt");
        Files.write(path, "test file content".getBytes());

        String expectedContent = "test file content";
        long expectedSize = Files.size(path);

        // MultiValueMap 생성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(path));

        // 요청 보내기
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("/files/save", requestEntity, String.class);

        // 검증
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getHeaders().getContentLength()).isEqualTo(expectedSize);

        String uploadedContent = new String(Files.readAllBytes(Paths.get("uploaded-file-path")));
        assertThat(uploadedContent).isEqualTo(expectedContent);
    }

    @Test
    @DisplayName("파일 업로드 시, 해당 경로, 같은 이름으로 FileStorage 테이블에 정보가 업데이트되는지 확안하기")
    void checkFileUpload_existInFileStorageTable(){

    }

    @Test
    @DisplayName("파일 업로드 시, 정확한 정보가 FileManager 테이블에 업데이트되는지 확인하기")
    void checkFileUpload_existInFileManagerTable(){

    }
}
