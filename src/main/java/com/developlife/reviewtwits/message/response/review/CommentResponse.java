package com.developlife.reviewtwits.message.response.review;

import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import lombok.Builder;

/**
 * @author WhalesBob
 * @since 2023-04-02
 */
public record CommentResponse(
        long commentId,
        UserInfoResponse userInfo,
        String content,
        long parentCommentId,
        long commentLikeCount
){
    @Builder
    public CommentResponse{

    }
}