package com.developlife.reviewtwits.file;

import com.developlife.reviewtwits.repository.FileInfoRepository;
import com.developlife.reviewtwits.repository.FileManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUploadTest {
    @Autowired
    public TestRestTemplate restTemplate;

    @Autowired
    public FileInfoRepository fileInfoRepository;

    @Autowired
    public FileManagerRepository fileManagerRepository;

    public ResponseEntity<String> fileUpload(String inputContent, String filename, String suffix, Long id, String referenceType) throws IOException {

        String fileFullName = filename + suffix;
        Path path = new File(System.getProperty("java.io.tmpdir"), fileFullName).toPath();

        Files.write(path, inputContent.getBytes());

        // MultiValueMap 생성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("attachedFiles", new FileSystemResource(path));
        body.add("id", id);
        body.add("referenceType", referenceType);

        // 요청 보내기
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        return restTemplate.postForEntity("/files/save", requestEntity, String.class);
    }
}
