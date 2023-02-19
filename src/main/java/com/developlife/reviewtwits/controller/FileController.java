package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.entity.FileInfo;
import com.developlife.reviewtwits.message.request.FileUpdateRequest;
import com.developlife.reviewtwits.repository.FileManagerRepository;
import com.developlife.reviewtwits.repository.FileInfoRepository;
import com.developlife.reviewtwits.service.FileStoreService;
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

    private final FileStoreService fileStore;

    @PostMapping("/files/save")
    public ResponseEntity<String> saveFile(@ModelAttribute FileUpdateRequest request) throws IOException {

        String referenceType = request.getReferenceType();
        Long id = request.getId();
        List<FileInfo> storeFiles = fileStore.storeFiles(request.getAttachedFiles(),id,referenceType);

        String storeFilename = storeFiles.get(0).getRealFilename();
        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFilename));

        return ResponseEntity.accepted().body(resource.toString());
    }
}
