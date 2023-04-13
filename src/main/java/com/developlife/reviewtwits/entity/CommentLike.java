package com.developlife.reviewtwits.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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

}