package com.developlife.reviewtwits.message.request;

import com.developlife.reviewtwits.message.annotation.file.ImageFiles;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author WhalesBob
 * @since 2023-03-24
 */
public record ImageUpdateRequest(
        @ImageFiles
        MultipartFile attachedFiles) {

    @Builder
    public ImageUpdateRequest{

    }
}