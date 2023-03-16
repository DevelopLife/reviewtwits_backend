package com.developlife.reviewtwits.file;

import com.developlife.reviewtwits.entity.FileInfo;
import com.developlife.reviewtwits.entity.FileManager;
import com.developlife.reviewtwits.service.FileStoreService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;

import javax.transaction.Transactional;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BasicFileUploadTest extends FileUploadTest {

    @Autowired
    private FileStoreService fileStore;

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
            checkFileUpload_existInDirectory("hello file upload", "templates",".txt");
        }

        void checkFileUpload_existInDirectory(String inputContent,String filename, String suffix) throws IOException{
            ResponseEntity<String> response = fileUpload(inputContent,filename,suffix, 12L, "Test");
            // 검증
            System.out.println("response.getStatusCode = " +  response.getStatusCode());
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            String storedPath = getStoredFullPath(response);
            String uploadedContent = new String(Files.readAllBytes(Path.of(storedPath)));
            assertThat(uploadedContent).isEqualTo(inputContent);
        }
    }

    private String getStoredFullPath(ResponseEntity<String> response) {
        return fileStore.getFullPath(response.getBody());
    }


    @Test
    @Transactional
    @DisplayName("파일 업로드 시, 해당 경로, 같은 이름으로 FileStorage, FileManager 테이블에 정보가 업데이트되는지 확안하기")
    void checkFileUpload_existInFileTable() throws IOException {
        String inputContent = "test for DB";
        Long id = 23L;
        String referenceType = "Test";
        ResponseEntity<String> response = fileUpload(inputContent,"testDB",".txt", id, referenceType);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        FileInfo updatedInfo = fileInfoRepository.findByOriginalFilename("testDB.txt").get();
        assertThat(updatedInfo.getFilePath()).isEqualTo(getStoredFullPath(response));

        String storedPath = getStoredFullPath(response);
        String uploadedContent = new String(Files.readAllBytes(Path.of(storedPath)));
        assertThat(uploadedContent).isEqualTo(inputContent);

        FileManager updatedFileManager = fileManagerRepository.findByFileInfo_FileID(updatedInfo.getFileID()).get();
        assertThat(updatedFileManager.getReferenceID()).isEqualTo(id);
        assertThat(updatedFileManager.getReferenceType()).isEqualTo(referenceType);
    }
}
