package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.entity.FileInfo;
import com.developlife.reviewtwits.exception.file.InvalidFilenameExtensionException;
import com.developlife.reviewtwits.message.request.FileUpdateRequest;
import com.developlife.reviewtwits.message.response.ErrorResponse;
import com.developlife.reviewtwits.service.FileStoreService;
import com.developlife.reviewtwits.type.ReferenceType;
import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.developlife.reviewtwits.handler.ExceptionHandlerTool.makeErrorResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileStoreService fileStore;
    // private final AwsS3Service s3Service;

    @PostMapping(value = "/files/save", produces = "application/json")
    public ResponseEntity<String> saveFile(@ModelAttribute FileUpdateRequest request) {

        String referenceType = request.referenceType();
        ReferenceType fileReferenceType = null;
        try{
            fileReferenceType = ReferenceType.valueOf(referenceType);
        } catch (IllegalArgumentException e){
            throw new InvalidFilenameExtensionException("허용된 파일 엔티티가 아닙니다.");
        }
        Long id = request.id();
        List<MultipartFile> attachedFiles = request.attachedFiles();

        List<FileInfo> storeFiles = fileStore.storeFiles(attachedFiles,id, fileReferenceType);
        String storeFilename = storeFiles.get(0).getRealFilename();

        return ResponseEntity.ok().body(storeFilename); // 바꾸기
    }

    @GetMapping(value = "/request-images/{UUID}")
    public Resource downloadImage(@PathVariable(name = "UUID") String fileName, HttpServletResponse response) throws IOException {

        if(ReferenceType.isValidFileType(ReferenceType.IMAGE, fileName)){
            String originalFilename = fileStore.findOriginalFilename(fileName);
            if(originalFilename == null){
                throw new FileNotFoundException("해당 파일이 존재하지 않습니다.");
            }
            response.setContentType(MediaType.IMAGE_JPEG.toString());
            // return s3Service.getFilesFromS3(fileName);
            return new UrlResource("file:"+ fileStore.getFullPath(fileName));
        }
        throw new InvalidFilenameExtensionException("등록된 이미지 파일 확장자로 온 요청이 아닙니다.");
    }

    @GetMapping(value = "/request-download-files/{UUID}", produces = "application/json")
    public ResponseEntity<Resource> downloadFile(@PathVariable(name = "UUID") String fileName) throws IOException {
        String originalFilename = fileStore.findOriginalFilename(fileName);

        if(originalFilename == null){
            throw new FileNotFoundException("해당 파일이 존재하지 않습니다.");
        }
        // Resource resource = s3Service.getFilesFromS3(fileName);
        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(fileName));

        String encodeDownloadFileName = UriUtils.encode(originalFilename, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename:\"" + encodeDownloadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .header(HttpHeaders.CONTENT_TYPE, ReferenceType.getContentType(fileName))
                .body(resource);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> attachedFileNullExceptionHandler(){
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<ErrorResponse> FileNotFoundExceptionHandler(){
        return makeErrorResponse(new FileNotFoundException("해당 파일이 존재하지 않습니다."), "fileName");
    }
}
