package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.ReviewScrap;
import com.developlife.reviewtwits.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewScrapRepository extends JpaRepository<ReviewScrap, Long> {
    Optional<ReviewScrap> findByReviewAndUser(Review review, User user);

    @Query("SELECT rs.review FROM ReviewScrap rs WHERE rs.user = :user")
    List<Review> findReviewByUser(User user);
}
