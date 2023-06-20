package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.RelatedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RelatedProductRepository extends JpaRepository<RelatedProduct, Long> {
}
