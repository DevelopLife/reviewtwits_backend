package com.developlife.reviewtwits.file;

import com.developlife.reviewtwits.entity.FileInfo;
import com.developlife.reviewtwits.entity.FileManager;
import com.developlife.reviewtwits.repository.FileInfoRepository;
import com.developlife.reviewtwits.repository.FileManagerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import javax.transaction.Transactional;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


public class BasicFileUploadTest extends FileUploadTest {

    @Autowired
    public FileInfoRepository fileInfoRepository;

    @Autowired
    public FileManagerRepository fileManagerRepository;

    @Nested
    class CheckFileUploadTest{
        @Test
        @DisplayName("파일 업로드 시, 파일이 저장되는지 확인하기")
        void checkFileUpload_existInDirectory_case1() throws IOException {
            String inputContent = "spring test files";
            String filename = "test2";
            String suffix = ".txt";

            ResponseEntity<String> response = fileUpload(inputContent,filename,suffix, 12L, "Test");
            System.out.println("response.getStatusCode = " +  response.getStatusCode());
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    private String getStoredFullPath(ResponseEntity<String> response) {
        return fileStoreService.getFullPath(response.getBody());
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

        FileManager updatedFileManager = fileManagerRepository.findByFileInfo_FileID(updatedInfo.getFileID()).get();
        assertThat(updatedFileManager.getReferenceId()).isEqualTo(id);
        assertThat(updatedFileManager.getReferenceType()).isEqualTo(referenceType);
    }
}
