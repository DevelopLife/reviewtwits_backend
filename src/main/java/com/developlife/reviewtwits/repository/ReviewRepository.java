package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findReviewsByProductUrl(String productURL);
    Page<Review> findByReviewIdLessThan(long reviewId, Pageable pageable);
}

