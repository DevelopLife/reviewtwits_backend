package com.developlife.reviewtwits.message.response.sns;

import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import lombok.Builder;

import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-04-09
 */
public record SnsReviewResponse(long reviewId,
                                UserInfoResponse userInfo,
                                List<String> reviewImageUrlList,
                                int commentCount,
                                int reactionCount) {
    @Builder
    public SnsReviewResponse{

    }
}