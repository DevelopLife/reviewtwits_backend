package com.developlife.reviewtwits.file;

import com.developlife.reviewtwits.entity.FileInfo;
import com.developlife.reviewtwits.message.request.FileUpdateRequest;
import com.developlife.reviewtwits.service.FileStoreService;
import com.developlife.reviewtwits.type.ReferenceType;
import com.developlife.reviewtwits.type.MadeMultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-03-12
 */
@SpringBootTest
@Component
public class FileDownloadSteps {

//    @MockBean
//    private AmazonS3 amazonS3;

    @Autowired
    private FileStoreService fileStoreService;

    public String textFileUpload(String inputContent, String filename,
                                 String suffix, Long id, String referenceType) throws IOException {

        String fileFullName = filename + suffix;
        File targetFile = new File(System.getProperty("java.io.tmpdir"), fileFullName);
        Path path = targetFile.toPath();

        Files.write(path, inputContent.getBytes());
        return fileResponse(id, referenceType,fileFullName, path).getBody();
    }

    public String imageFileUpload(Long id, String referenceType) throws IOException{
        String fileFullName = "image.png";
        File targetFile = new File(System.getProperty("java.io.tmpdir"), fileFullName);

        BufferedImage image = new BufferedImage(200,200,BufferedImage.TYPE_INT_ARGB);
        ImageIO.write(image,"png",targetFile);

        return fileResponse(id, referenceType,fileFullName, targetFile.toPath()).getBody();
    }

    private ResponseEntity<String> fileResponse(Long id, String referenceType,String fileName, Path path) throws IOException {

        FileUpdateRequest request = FileUpdateRequest.builder()
                .attachedFiles(List.of(new MadeMultipartFile(Files.readAllBytes(path),fileName)))
                .id(id)
                .referenceType(referenceType)
                .build();

        return mockFileUploadProcess(request);
    }

    public ResponseEntity<String> mockFileUploadProcess(FileUpdateRequest request){
        // doReturn(null).when(amazonS3).putObject(Mockito.any(PutObjectRequest.class));

        try {
            ReferenceType referenceType = ReferenceType.valueOf(request.referenceType());
            List<FileInfo> fileInfoList = fileStoreService.storeFiles(request.attachedFiles(), request.id(), referenceType);
            String storeFilename = fileInfoList.get(0).getRealFilename();
            return ResponseEntity.ok().body(storeFilename);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("잘못된 referenceType 입니다.");
        }
    }
}