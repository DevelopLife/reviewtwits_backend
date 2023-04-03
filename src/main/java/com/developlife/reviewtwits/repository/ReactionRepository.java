package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.Reaction;
import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    List<Reaction> findByReview(Review review);
    Optional<Reaction> findByReview_ReviewIdAndUser(long reviewId, User user);
}
