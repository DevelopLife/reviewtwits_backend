package com.developlife.reviewtwits.repository.review;

import com.developlife.reviewtwits.entity.Comment;
import com.developlife.reviewtwits.entity.CommentLike;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * @author WhalesBob
 * @since 2023-05-23
 */
@Getter
@Setter
public class CommentMappingDTO {
    private Comment comment;
    private Set<CommentLike> commentLikeSet;

    @QueryProjection
    public CommentMappingDTO(Comment comment, Set<CommentLike> commentLikeSet) {
        this.comment = comment;
        this.commentLikeSet = commentLikeSet;
    }
}