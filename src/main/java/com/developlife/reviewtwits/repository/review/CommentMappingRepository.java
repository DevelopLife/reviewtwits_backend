package com.developlife.reviewtwits.repository.review;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.response.review.CommentResponse;

import java.util.List;

public interface CommentMappingRepository {
    List<CommentResponse> findByReview_ReviewId(long reviewId, User user);
}
