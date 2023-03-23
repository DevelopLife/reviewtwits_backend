package com.developlife.reviewtwits.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * @author WhalesBob
 * @since 2023-03-22
 */

@Entity
@Builder
@Getter
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

    @Transient
    private String fileName;
}