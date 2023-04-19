package com.developlife.reviewtwits.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author WhalesBob
 * @since 2023-03-22
 */

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RelatedProduct {

    @Id
    @GeneratedValue
    private long productId;

    private String productUrl;

    private String name;

    private int price;

    private String imagePath;

    private String imageUuid;
}