package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.Comment;
import com.developlife.reviewtwits.entity.CommentLike;
import com.developlife.reviewtwits.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByUserAndComment(User user, Comment comment);
    Optional<CommentLike> findByUserAndComment(User user, Comment comment);
}
