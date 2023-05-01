package com.developlife.reviewtwits.repository.review;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.response.sns.DetailSnsReviewResponse;
import org.springframework.data.domain.Pageable;


import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-04-16
 */
public interface ReviewMappingRepository {
    List<DetailSnsReviewResponse> findMappingReviewScrappedByUser(User user, Pageable pageable);
    List<DetailSnsReviewResponse> findMappingReviewById(User user,Long reviewId, Pageable pageable);
    List<DetailSnsReviewResponse> findMappingReviewByProductNameLikeOrContentLike(String searchKey, User reviewSearcher, Pageable pageable);
    DetailSnsReviewResponse findOneMappingReviewById(User user, long reviewId);

}