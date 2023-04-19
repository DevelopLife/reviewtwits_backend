package com.developlife.reviewtwits.repository.review;

import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.mapper.ReviewMapper;
import com.developlife.reviewtwits.message.response.review.ReactionResponse;
import com.developlife.reviewtwits.message.response.sns.DetailSnsReviewResponse;
import com.developlife.reviewtwits.type.ReactionType;
import com.developlife.reviewtwits.type.ReferenceType;
import com.querydsl.core.group.Group;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private final ReviewMapper reviewMapper;


    @Override
    public List<DetailSnsReviewResponse> findMappingReviewByUser(User user, Pageable pageable) {
        return findMappingReview(user, user,null, pageable);
    }

    @Override
    public List<DetailSnsReviewResponse> findMappingReviewById(User user,Long reviewId, Pageable pageable) {
        return findMappingReview(null, user,reviewId, pageable);
    }

    @Override
    public List<DetailSnsReviewResponse> findMappingReviewByProductNameLikeOrContentLike(String searchKey, User reviewSearcher, Pageable pageable) {
        QBean<ReviewMappingDTO> bean = getReviewMappingDTOQBean();

        BooleanExpression findProductNameOrContent = review.exist.isTrue().and(review.productName.like(searchKey).or(review.content.like(searchKey)));

        jpaQueryFactory.select(bean)
                .from(review, fileInfo, fileManager)
                .leftJoin(reaction).on(review.eq(reaction.review))
                .leftJoin(reviewScrap).on(reviewScrap.review.eq(review).and(reviewScrap.user.eq(reviewSearcher)))
                .where(findProductNameOrContent
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
        return null;
    }

    public List<DetailSnsReviewResponse> findMappingReview(User reviewWriter,User reviewSearcher, Long reviewId, Pageable pageable){

        QBean<ReviewMappingDTO> bean = getReviewMappingDTOQBean();

        BooleanExpression lessThanReviewId = getExpressionOfId(reviewId);
        BooleanExpression findByUser = getExpressionOfUser(reviewWriter);
        BooleanExpression userOnReviewScrap = getReviewScrapOfUser(reviewSearcher);

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

        return getDetailSnsReviewResponses(reviewSearcher, transform);
    }

    private QBean<ReviewMappingDTO> getReviewMappingDTOQBean() {
        QBean<ReviewMappingDTO> bean = Projections.bean(
                ReviewMappingDTO.class,
                review,
                list(fileInfo.realFilename).as("reviewImageNameList"),
                list(reaction).as("reactionList"),
                set(reviewScrap).as("reviewScrap")
        );
        return bean;
    }

    private List<DetailSnsReviewResponse> getDetailSnsReviewResponses(User reviewSearcher, Map<Review, Group> transform) {
        List<ReviewMappingDTO> reviewMappingList = transform.entrySet().stream()
                .map(entry -> new ReviewMappingDTO(
                        entry.getKey(),
                        entry.getValue().getList(fileInfo.realFilename),
                        entry.getValue().getList(reaction),
                        entry.getValue().getSet(reviewScrap))).toList();

        List<DetailSnsReviewResponse> snsResponse = new ArrayList<>();

        for(ReviewMappingDTO reviewMappingDTO : reviewMappingList){
            Review review = reviewMappingDTO.getReview();
            review.setReviewImageUuidList(reviewMappingDTO.getReviewImageNameList());

            Map<String, ReactionResponse> collectedReactionResponse = ReactionType.classifyReactionResponses(
                    reviewSearcher,
                    reviewMappingDTO.getReactionList()
            );

            boolean isScrapped = !reviewMappingDTO.getReviewScrap().isEmpty();
            snsResponse.add(reviewMapper.toDetailSnsReviewResponse(review, collectedReactionResponse,isScrapped));
        }

        return snsResponse;
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