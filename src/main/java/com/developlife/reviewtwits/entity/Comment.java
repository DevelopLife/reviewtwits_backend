package com.developlife.reviewtwits.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

/**
 * @author WhalesBob
 * @since 2023-04-01
 */
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseEntity {

    @Id @GeneratedValue
    private long commentId;

    @ManyToOne
    private Review review;

    @ManyToOne
    private User user;

    private String content;

    @ManyToOne
    private Comment commentGroup;

    @Builder.Default
    @ColumnDefault(value = "0")
    private int commentLike = 0;
}