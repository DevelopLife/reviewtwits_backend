package com.developlife.reviewtwits.message.response.review;

import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import com.developlife.reviewtwits.type.review.ReviewStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-03-17
 */
public record DetailShoppingMallReviewResponse(LocalDateTime createdDate,
                                               LocalDateTime lastModifiedDate,
                                               long reviewId,
                                               UserInfoResponse userInfo,
                                               long projectId,
                                               String content,
                                               String productUrl,
                                               String productName,
                                               int score,
                                               List<String> reviewImageUrlList,
                                               int reactionCount,
                                               boolean isLiked,
                                               ReviewStatus status){

    @Builder
    public DetailShoppingMallReviewResponse {}
}