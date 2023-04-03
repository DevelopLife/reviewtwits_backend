package com.developlife.reviewtwits.entity;

import com.developlife.reviewtwits.type.ReactionType;
import lombok.*;

import javax.persistence.*;

/**
 * @author WhalesBob
 * @since 2023-04-02
 */

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reaction extends BaseEntity {

    @Id @GeneratedValue
    private long reactionId;

    @ManyToOne
    private User user;

    @ManyToOne
    private Review review;

    @Enumerated(value = EnumType.STRING)
    private ReactionType reactionType;

}