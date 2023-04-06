package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.ReviewScrap;
import com.developlife.reviewtwits.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewScrapRepository extends JpaRepository<ReviewScrap, Long> {
    Optional<ReviewScrap> findByReviewAndUser(Review review, User user);
}
