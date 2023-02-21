package com.developlife.reviewtwits.message.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor @Getter @Setter
public class FileUpdateRequest {

    private Long id; // userID 로 고쳐서 진행할 것.
    private String referenceType;
    private List<MultipartFile> attachedFiles;

}
