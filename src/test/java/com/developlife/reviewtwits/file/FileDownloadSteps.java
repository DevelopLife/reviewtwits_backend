package com.developlife.reviewtwits.file;

import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author WhalesBob
 * @since 2023-03-12
 */
@Component
public class FileDownloadSteps {

    @Autowired
    public TestRestTemplate restTemplate;

    public String textFileUpload(String inputContent, String filename, String suffix, Long id, String referenceType) throws IOException {

        String fileFullName = filename + suffix;
        File targetFile = new File(System.getProperty("java.io.tmpdir"), fileFullName);
        Path path = targetFile.toPath();

        Files.write(path, inputContent.getBytes());
        return fileResponse(id, referenceType, path).getBody();
    }

    public String imageFileUpload(Long id, String referenceType) throws IOException{
        String fileFullName = "image.png";
        File targetFile = new File(System.getProperty("java.io.tmpdir"), fileFullName);

        BufferedImage image = new BufferedImage(200,200,BufferedImage.TYPE_INT_ARGB);
        ImageIO.write(image,"png",targetFile);

        return fileResponse(id, referenceType, targetFile.toPath()).getBody();
    }

    private ResponseEntity<String> fileResponse(Long id, String referenceType, Path path) {

        // 요청 보내기
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