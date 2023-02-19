package com.developlife.reviewtwits.message.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter @Setter @RequiredArgsConstructor @EqualsAndHashCode @ToString
public class FileUpdateRequest {

    private Long id;
    private String fileName;
    private List<MultipartFile> attachedFiles;

}
