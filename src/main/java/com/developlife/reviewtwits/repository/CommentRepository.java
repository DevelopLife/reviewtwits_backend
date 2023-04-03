package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByReview_ReviewId(long reviewId);
}
