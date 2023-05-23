package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.Comment;
import com.developlife.reviewtwits.repository.review.CommentMappingRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentMappingRepository {
}
