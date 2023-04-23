package com.developlife.reviewtwits.message.response.statistics;

import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import lombok.Builder;

/**
 * @author WhalesBob
 * @since 2023-04-23
 */
public record SaveStatResponse(
        long statId,
        String createdDate,
        String inflowUrl,
        String productUrl,
        long projectId,
        long productId,
        UserInfoResponse userInfo,
        String deviceInfo
) {
    @Builder
    public SaveStatResponse {
    }
}