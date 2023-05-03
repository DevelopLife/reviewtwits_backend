package com.developlife.reviewtwits.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author WhalesBob
 * @since 2023-03-13
 */

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id @GeneratedValue
    private long productId;

    @Column(name = "path")
    private String productUrl;

    @ManyToOne
    @JoinColumn(name = "projectId")
    private Project project;

    private String imageUrl;

    private String productName;
}
