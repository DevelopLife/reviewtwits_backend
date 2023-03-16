package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author WhalesBob
 * @since 2023-03-13
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findProductByProductUrl(String productUrl);
    boolean existsProductByProductUrl(String productURL);
}