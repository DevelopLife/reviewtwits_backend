package com.developlife.reviewtwits.message.request.review;

import com.developlife.reviewtwits.message.annotation.file.ImageFiles;
import com.developlife.reviewtwits.message.annotation.review.DeleteFileName;
import com.developlife.reviewtwits.message.annotation.review.MultipartInteger;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.validation.constraints.Size;
import java.util.List;

public record ShoppingMallReviewChangeRequest(
        @Nullable
        @Size(message = "리뷰내용은 10자 이상이어야 합니다.",min = 10)
        String content,
        @Nullable
        @MultipartInteger
        String score,
        @ImageFiles
        List<MultipartFile> multipartImageFiles,
        @DeleteFileName
        List<String> deleteFileList) {

}