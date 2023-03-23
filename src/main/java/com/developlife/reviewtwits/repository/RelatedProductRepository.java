package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.RelatedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatedProductRepository extends JpaRepository<RelatedProduct, Long> {
    boolean existsByNameLike(String name);
}
