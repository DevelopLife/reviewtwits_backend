package com.developlife.reviewtwits.repository.review;

import com.developlife.reviewtwits.mapper.ReviewMapper;
import com.developlife.reviewtwits.message.response.review.CommentResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.developlife.reviewtwits.entity.QComment.comment;
import static com.developlife.reviewtwits.entity.QCommentLike.commentLike;

/**
 * @author WhalesBob
 * @since 2023-05-23
 */
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentMappingRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final ReviewMapper reviewMapper;

    @Override
    public List<CommentResponse> findByReview_ReviewId(long reviewId) {
        List<CommentMappingDTO> commentMappingDTO = jpaQueryFactory.select(Projections.constructor(
                        CommentMappingDTO.class,
                        comment,
                        commentLike.count().as("commentLikeCount"))).from(comment)
                .leftJoin(commentLike).on(comment.eq(commentLike.comment))
                .where(comment.review.reviewId.eq(reviewId))
                .groupBy(comment)
                .fetch();

        return reviewMapper.toCommentResponseList(commentMappingDTO);
    }
}