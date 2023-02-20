package com.developlife.reviewtwits.file;

import com.developlife.reviewtwits.entity.FileInfo;
import com.developlife.reviewtwits.entity.FileManager;
import com.developlife.reviewtwits.repository.FileInfoRepository;
import com.developlife.reviewtwits.repository.FileManagerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BasicFileUploadTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Autowired
    private FileManagerRepository fileManagerRepository;

    private ResponseEntity<String> fileUpload(String inputContent, String filename, String suffix, Long id, String referenceType) throws IOException {

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
        ResponseEntity<String> response = restTemplate.postForEntity("/files/save", requestEntity, String.class);
        return response;
    }

    @Nested
    class CheckFileUploadTest{

        @Test
        @DisplayName("파일 업로드 시, 같은 내용으로 파일이 존재하는지 확안하기.")
        void checkFileUpload_existInDirectory_case1() throws IOException {
            checkFileUpload_existInDirectory("spring test files","test2",".txt");
        }

        @Test
        @DisplayName("파일 업로드 시, 같은 내용으로 파일이 존재하는지 확안하기.")
        void checkFileUpload_existInDirectory_case2() throws IOException {
            checkFileUpload_existInDirectory("hello file upload","test",".txt");
        }

        void checkFileUpload_existInDirectory(String inputContent,String filename, String suffix) throws IOException{
            ResponseEntity<String> response = fileUpload(inputContent,filename,suffix, null, null);
            // 검증
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

            String storedPath = getStoredFullPath(response);
            String uploadedContent = new String(Files.readAllBytes(Path.of(storedPath)));
            assertThat(uploadedContent).isEqualTo(inputContent);
        }
    }

    private String getStoredFullPath(ResponseEntity<String> response) {
        return response.getBody().substring(10, response.getBody().length() - 1);
    }


    @Test
    @Transactional
    @DisplayName("파일 업로드 시, 해당 경로, 같은 이름으로 FileStorage, FileManager 테이블에 정보가 업데이트되는지 확안하기")
    void checkFileUpload_existInFileTable() throws IOException {
        String inputContent = "test for DB";
        Long id = 23L;
        String referenceType = "User";
        ResponseEntity<String> response = fileUpload(inputContent,"testDB",".txt", id, referenceType);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        FileInfo updatedInfo = fileInfoRepository.findByOriginalFilename("testDB.txt").get();
        assertThat(updatedInfo.getFilePath()).isEqualTo(getStoredFullPath(response));

        String storedPath = getStoredFullPath(response);
        String uploadedContent = new String(Files.readAllBytes(Path.of(storedPath)));
        assertThat(uploadedContent).isEqualTo(inputContent);

        FileManager updatedFileManager = fileManagerRepository.findByFileStorageID(updatedInfo.getFileID()).get();
        assertThat(updatedFileManager.getReferenceID()).isEqualTo(id);
        assertThat(updatedFileManager.getReferenceType()).isEqualTo(referenceType);
    }
}
