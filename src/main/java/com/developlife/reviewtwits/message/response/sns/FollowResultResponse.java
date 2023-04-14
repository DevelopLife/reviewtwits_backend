package com.developlife.reviewtwits.message.response.sns;

import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import lombok.Builder;

public record FollowResultResponse(long followId,
                                   UserInfoResponse userInfoResponse,
                                   UserInfoResponse targetUserInfoResponse,
                                   boolean followBackFlag) {

    @Builder
    public FollowResultResponse {
    }
}
