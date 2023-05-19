package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.type.ReferenceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author ghdic
 * @since 2023/04/07
 */
@RequiredArgsConstructor
@Component
public class ReviewUtils {

    private final FileStoreService fileStoreService;

    public void saveReviewImage(Review review){
        review.setReviewImageUuidList(fileStoreService.bringFileNameList(ReferenceType.REVIEW, review.getReviewId()));
    }
}
