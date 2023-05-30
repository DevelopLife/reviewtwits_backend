package com.developlife.reviewtwits.repository.review;

import com.developlife.reviewtwits.entity.Comment;
import com.developlife.reviewtwits.entity.CommentLike;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.mapper.ReviewMapper;
import com.developlife.reviewtwits.message.response.review.CommentResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.developlife.reviewtwits.entity.QComment.comment;
import static com.developlife.reviewtwits.entity.QCommentLike.commentLike;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;

/**
 * @author WhalesBob
 * @since 2023-05-23
 */
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentMappingRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final ReviewMapper reviewMapper;

    @Override
    public List<CommentResponse> findByReview_ReviewId(long reviewId, User user) {
        Map<Comment, Set<CommentLike>> commentLikeMap = jpaQueryFactory.select(Projections.bean(
                        CommentMappingDTO.class,
                        comment,
                        set(commentLike).as("commentLikeSet"))).from(comment)
                .leftJoin(commentLike).on(comment.eq(commentLike.comment))
                .where(comment.review.reviewId.eq(reviewId))
                .transform(groupBy(comment).as(set(commentLike)));

        List<CommentResponse> response = new ArrayList<>();

        for (Map.Entry<Comment, Set<CommentLike>> commentEntry : commentLikeMap.entrySet()) {
            response.add(makeCommentResponse(commentEntry.getKey(), commentEntry.getValue(), user));
        }
        return response;
    }

    private CommentResponse makeCommentResponse(Comment comment, Set<CommentLike> commentLikeSet, User user) {
        return CommentResponse.builder()
                .createdDate(comment.getCreatedDate())
                .commentId(comment.getCommentId())
                .userInfo(reviewMapper.mapUserToUserInfoResponse(comment.getUser()))
                .content(comment.getContent())
                .parentCommentId(comment.getParentId())
                .commentLikeCount(commentLikeSet.size())
                .isCommentLiked(commentLikeSet.stream().anyMatch(commentLike -> commentLike.getUser().equals(user)))
                .build();
    }
}