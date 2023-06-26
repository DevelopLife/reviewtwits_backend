package com.developlife.reviewtwits.entity;

import lombok.*;

import javax.persistence.*;

/**
 * @author WhalesBob
 * @since 2023-04-10
 */

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentLike {

    @Id @GeneratedValue
    private long commentLikeId;

    @ManyToOne
    private User user;

    @ManyToOne
    private Comment comment;

    @PrePersist
    private void preMakingCommentLike() {
        this.comment.setCommentLike(this.comment.getCommentLike() + 1);
    }

    @PreRemove
    private void preRemovingCommentLike() {
        this.comment.setCommentLike(this.comment.getCommentLike() - 1);
    }
}