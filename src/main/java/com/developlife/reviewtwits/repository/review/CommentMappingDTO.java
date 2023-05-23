package com.developlife.reviewtwits.repository.review;

import com.developlife.reviewtwits.entity.Comment;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

/**
 * @author WhalesBob
 * @since 2023-05-23
 */
@Getter
@Setter
public class CommentMappingDTO {
    private Comment comment;
    private long commentLikeCount;

    @QueryProjection
    public CommentMappingDTO(Comment comment, long commentLikeCount) {
        this.comment = comment;
        this.commentLikeCount = commentLikeCount;
    }
}