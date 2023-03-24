package com.developlife.reviewtwits.message.request;

import com.developlife.reviewtwits.message.annotation.file.ImageFile;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author WhalesBob
 * @since 2023-03-24
 */
public record ImageUpdateRequest(
        @ImageFile
        MultipartFile imageFile) {

    @Builder
    public ImageUpdateRequest{

    }
}