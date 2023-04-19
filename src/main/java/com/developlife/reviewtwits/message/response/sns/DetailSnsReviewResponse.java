package com.developlife.reviewtwits.message.response.sns;

import com.developlife.reviewtwits.message.response.review.ReactionResponse;
import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author WhalesBob
 * @since 2023-04-02
 */
public record DetailSnsReviewResponse(LocalDateTime createdDate,
                                      LocalDateTime lastModifiedDate,
                                      long reviewId,
                                      UserInfoResponse userInfo,
                                      String content,
                                      String productUrl,
                                      String productName,
                                      int score,
                                      List<String> reviewImageUrlList,
                                      int commentCount,
                                      Map<String, ReactionResponse> reactionResponses,
                                      boolean isScrapped) {

    @Builder
    public DetailSnsReviewResponse{

    }
}