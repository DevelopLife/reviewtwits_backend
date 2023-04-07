package com.developlife.reviewtwits.entity;

import lombok.*;

import javax.persistence.*;

/**
 * @author WhalesBob
 * @since 2023-04-07
 */

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewScrap {

    @Id @GeneratedValue
    private long reviewScrapId;

    @ManyToOne
    private Review review;

    @ManyToOne
    private User user;

}