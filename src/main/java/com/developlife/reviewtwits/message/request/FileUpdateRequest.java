package com.developlife.reviewtwits.message.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record FileUpdateRequest(Long id, String referenceType, List<MultipartFile> attachedFiles) {
    @Builder
    public FileUpdateRequest{

    }

}
