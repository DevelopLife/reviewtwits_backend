package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findReviewsByProductUrl(String productURL);
    Page<Review> findByReviewIdLessThan(long reviewId, Pageable pageable);

    List<Review> findReviewsByUser(User user);

    @Query("SELECT r FROM Review r WHERE r.exist = true AND (r.productName LIKE %:searchKey% OR r.content LIKE %:searchKey%) ORDER BY r.reviewId DESC")
    List<Review> findByProductNameLikeOrContentLike(@Param("searchKey")String searchKey, Pageable pageable);
}

