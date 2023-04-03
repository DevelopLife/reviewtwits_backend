package com.developlife.reviewtwits.message.response.review;

import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

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
                                      List<String> reviewImageNameList,
                                      int commentCount,
                                      List<ReactionResponse> reactionResponseList,
                                      boolean exist) {

    @Builder
    public DetailSnsReviewResponse{

    }
}