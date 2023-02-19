package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.message.request.FileUpdateRequest;
import com.developlife.reviewtwits.repository.FileRepository;
import com.developlife.reviewtwits.service.FileStoreService;
import com.developlife.reviewtwits.type.UploadFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileRepository fileRepository;
    private final FileStoreService fileStore;

    @PostMapping("/files/save")
    public ResponseEntity<String> saveFile(@ModelAttribute FileUpdateRequest request) throws IOException {

        log.info("request = {}",request);
        List<UploadFile> storeFiles = fileStore.storeFiles(request.getAttachedFiles());

        String storeFilename = storeFiles.get(0).getStoreFileName();
        log.info("storeFilename = {}", storeFilename);
        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFilename));
        log.info("resource in controller = {}",resource);

        return ResponseEntity.accepted().body(resource.toString());
    }
}
