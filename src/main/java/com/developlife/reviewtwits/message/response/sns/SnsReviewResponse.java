package com.developlife.reviewtwits.message.response.sns;

import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import lombok.Builder;

/**
 * @author WhalesBob
 * @since 2023-04-09
 */
public record SnsReviewResponse(long reviewId,
                                UserInfoResponse userInfo,
                                String reviewImage,
                                int commentCount,
                                int reactionCount) {
    @Builder
    public SnsReviewResponse{

    }
}