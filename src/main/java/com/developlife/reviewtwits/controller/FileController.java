package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.entity.FileInfo;
import com.developlife.reviewtwits.exception.file.InvalidFilenameExtensionException;
import com.developlife.reviewtwits.message.request.FileUpdateRequest;
import com.developlife.reviewtwits.message.response.ErrorResponse;
import com.developlife.reviewtwits.service.FileStoreService;
import com.developlife.reviewtwits.type.FileReferenceType;
import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.developlife.reviewtwits.handler.ExceptionHandlerTool.makeErrorResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileStoreService fileStore;

    @PostMapping(value = "/files/save", produces = "application/json")
    public ResponseEntity<String> saveFile(@ModelAttribute FileUpdateRequest request) {

        String referenceType = request.referenceType();
        Long id = request.id();
        List<MultipartFile> attachedFiles = request.attachedFiles();
        if(attachedFiles.get(0).isEmpty() || !FileReferenceType.isValidFileType(referenceType, attachedFiles)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<FileInfo> storeFiles = fileStore.storeFiles(attachedFiles,id,referenceType);
        String storeFilename = storeFiles.get(0).getRealFilename();

        return ResponseEntity.ok().body(storeFilename); // 바꾸기
    }

    @GetMapping("/request-images/{UUID}")
    public Resource downloadImage(@PathVariable(name = "UUID") String fileName) throws MalformedURLException {
        if(FileReferenceType.isValidFileType("image",fileName)){
            return new UrlResource("file:"+ fileStore.getFullPath(fileName));
        }
        throw new InvalidFilenameExtensionException("등록된 이미지 파일 확장자로 온 요청이 아닙니다.");
    }

    @GetMapping(value = "/request-download-files/{UUID}", produces = "application/json")
    public ResponseEntity<Resource> downloadFile(@PathVariable(name = "UUID") String fileName) throws MalformedURLException{
        String originalFilename = fileStore.findOriginalFilename(fileName);

        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(fileName));

        String encodeDownloadFileName = UriUtils.encode(originalFilename, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename:\"" + encodeDownloadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .header(HttpHeaders.CONTENT_TYPE, FileReferenceType.getContentType(fileName))
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
