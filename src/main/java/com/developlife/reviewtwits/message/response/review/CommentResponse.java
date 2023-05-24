package com.developlife.reviewtwits.message.response.review;

import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * @author WhalesBob
 * @since 2023-04-02
 */
public record CommentResponse(
        LocalDateTime createdDate,
        long commentId,
        UserInfoResponse userInfo,
        String content,
        long parentCommentId,
        long commentLikeCount,
        boolean isCommentLiked
){
    @Builder
    public CommentResponse{

    }
}