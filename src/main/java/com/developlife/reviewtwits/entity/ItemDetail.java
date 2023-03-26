package com.developlife.reviewtwits.entity;

import lombok.*;

import javax.persistence.*;

/**
 * @author WhalesBob
 * @since 2023-03-24
 */

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDetail {

    @Id
    @GeneratedValue
    private long itemId;

    @OneToOne
    private RelatedProduct relatedProduct;

    @Column(length = 10000)
    private String detailInfo;
}