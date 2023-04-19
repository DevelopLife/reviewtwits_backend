package com.developlife.reviewtwits.repository.review;

import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.type.ReferenceType;
import com.querydsl.core.group.Group;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.developlife.reviewtwits.entity.QFileInfo.fileInfo;
import static com.developlife.reviewtwits.entity.QFileManager.fileManager;
import static com.developlife.reviewtwits.entity.QReaction.reaction;
import static com.developlife.reviewtwits.entity.QReview.review;
import static com.developlife.reviewtwits.entity.QReviewScrap.reviewScrap;
import static com.querydsl.core.group.GroupBy.*;

/**
 * @author WhalesBob
 * @since 2023-04-16
 */

@RequiredArgsConstructor
public class ReviewMappingRepositoryImpl implements ReviewMappingRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ReviewMappingDTO> findMappingReviewByUser(User user, Pageable pageable) {
        return findMappingReview(user, user,null, pageable);
    }

    @Override
    public List<ReviewMappingDTO> findMappingReviewById(User user,Long reviewId, Pageable pageable) {
        return findMappingReview(null, user,reviewId, pageable);
    }

    public List<ReviewMappingDTO> findMappingReview(User userForFind,User userForScrap, Long reviewId, Pageable pageable){

        QBean<ReviewMappingDTO> bean = Projections.bean(
                ReviewMappingDTO.class,
                review,
                list(fileInfo.realFilename).as("reviewImageNameList"),
                list(reaction).as("reactionList"),
                set(reviewScrap).as("reviewScrap")
        );

        BooleanExpression lessThanReviewId = getExpressionOfId(reviewId);
        BooleanExpression findByUser = getExpressionOfUser(userForFind);
        BooleanExpression userOnReviewScrap = getReviewScrapOfUser(userForScrap);

        Map<Review, Group> transform = jpaQueryFactory.select(bean)
                .from(review, fileInfo, fileManager)
                .leftJoin(reaction).on(review.eq(reaction.review))
                .leftJoin(reviewScrap).on(reviewScrap.review.eq(review).and(userOnReviewScrap))
                .where(lessThanReviewId.and(findByUser)
                        .and(fileInfo.eq(fileManager.fileInfo))
                        .and(fileInfo.exist.isTrue())
                        .and(fileManager.referenceId.eq(review.reviewId)
                                .and(fileManager.referenceType.eq(ReferenceType.REVIEW))))
                .orderBy(review.reviewId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .transform(
                        groupBy(review).as(
                                list(fileInfo.realFilename),
                                list(reaction),
                                set(reviewScrap)));

        return transform.entrySet().stream()
                .map(entry -> new ReviewMappingDTO(
                        entry.getKey(),
                        entry.getValue().getList(fileInfo.realFilename),
                        entry.getValue().getList(reaction),
                        entry.getValue().getSet(reviewScrap)))
                .collect(Collectors.toList());
    }

    private BooleanExpression getReviewScrapOfUser(User user) {
        if(user == null){
            return reviewScrap.reviewScrapId.isNull();
        }
        return reviewScrap.user.eq(user);
    }

    private BooleanExpression getExpressionOfUser(User user) {
        if(user == null){
            return review.user.isNotNull();
        }
        return review.user.eq(user);
    }

    private BooleanExpression getExpressionOfId(Long reviewId) {
        if(reviewId == null){
            return review.reviewId.isNotNull();
        }
        return review.reviewId.lt(reviewId);
    }
}