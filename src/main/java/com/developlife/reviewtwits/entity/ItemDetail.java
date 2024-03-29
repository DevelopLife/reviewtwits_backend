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
public class ItemDetail extends BaseEntity {

    @Id
    @GeneratedValue
    private long itemId;

    private String productName;

    @OneToOne
    private RelatedProduct relatedProduct;

    @Column(length = 10000)
    private String detailInfo;

    @Builder.Default
    private double score = 0;
    @Builder.Default
    private int reviewCount = 0;
}