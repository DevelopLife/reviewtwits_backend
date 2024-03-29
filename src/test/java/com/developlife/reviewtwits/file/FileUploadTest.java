package com.developlife.reviewtwits.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.developlife.reviewtwits.entity.FileInfo;
import com.developlife.reviewtwits.exception.file.FileEmptyException;
import com.developlife.reviewtwits.exception.file.InvalidFilenameExtensionException;
import com.developlife.reviewtwits.message.request.FileUpdateRequest;
import com.developlife.reviewtwits.service.FileStoreService;
import com.developlife.reviewtwits.type.MadeMultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileUploadTest {

//    @Autowired
//    private AmazonS3 s3Client;

    @Autowired
    public TestRestTemplate restTemplate;

    @Autowired
    protected FileStoreService fileStoreService;

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


//    public ResponseEntity<String> fileUpload(String inputContent, String filename, String suffix, Long id, String referenceType) throws IOException {
//
//        String fileFullName = filename + suffix;
//        Path path = new File(System.getProperty("java.io.tmpdir"), fileFullName).toPath();
//
//        Files.write(path, inputContent.getBytes());
//
//        byte[] fileInByte = Files.readAllBytes(path);
//        FileUpdateRequest request = FileUpdateRequest.builder()
//                .attachedFiles(List.of(new MadeMultipartFile(fileInByte,fileFullName)))
//                .id(id)
//                .referenceType(referenceType)
//                .build();
//
//        return mockFileUploadProcess(request);
//    }
//
//    public ResponseEntity<String> mockFileUploadProcess(FileUpdateRequest request){
//
//        doReturn(new PutObjectResult()).when(s3Client).putObject(any(PutObjectRequest.class));
//        try{
//            List<FileInfo> fileInfoList = fileStoreService.storeFiles(request.attachedFiles(), request.id(), request.referenceType());
//            verify(s3Client).putObject(any(PutObjectRequest.class));
//            String storeFilename = fileInfoList.get(0).getRealFilename();
//            return ResponseEntity.ok().body(storeFilename);
//        }catch(FileEmptyException  | InvalidFilenameExtensionException e){
//            return ResponseEntity.badRequest().body("");
//        }
//    }
}
