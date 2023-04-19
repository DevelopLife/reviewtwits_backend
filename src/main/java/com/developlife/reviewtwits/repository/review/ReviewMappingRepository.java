package com.developlife.reviewtwits.repository.review;

import com.developlife.reviewtwits.entity.User;
import org.springframework.data.domain.Pageable;


import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-04-16
 */
public interface ReviewMappingRepository {
    List<ReviewMappingDTO> findMappingReviewByUser(User user, Pageable pageable);
    List<ReviewMappingDTO> findMappingReviewById(User user,Long reviewId, Pageable pageable);
}