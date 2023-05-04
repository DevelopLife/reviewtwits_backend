package com.developlife.reviewtwits.entity;

import lombok.*;

import javax.persistence.*;

/**
 * @author WhalesBob
 * @since 2023-03-13
 */

@Entity
@Builder
@Getter
@Setter
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
