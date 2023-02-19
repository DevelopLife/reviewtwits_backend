package com.developlife.reviewtwits.message.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter @Setter @RequiredArgsConstructor @EqualsAndHashCode
public class FileUpdateRequest {

    private Long id;
    private String fileName;
    private List<MultipartFile> attachedFiles;

}
