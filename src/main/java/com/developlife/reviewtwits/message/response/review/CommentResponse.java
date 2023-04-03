package com.developlife.reviewtwits.message.response.review;

import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import com.developlife.reviewtwits.repository.CommentRepository;
import lombok.Builder;

/**
 * @author WhalesBob
 * @since 2023-04-02
 */
public record CommentResponse(
        long commentId,
        UserInfoResponse userInfo,
        String content,
        long parentCommentId
){
    @Builder
    public CommentResponse{

    }
}