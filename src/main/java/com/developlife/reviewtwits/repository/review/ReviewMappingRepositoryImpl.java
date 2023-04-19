package com.developlife.reviewtwits.repository.review;

import com.developlife.reviewtwits.entity.QReview;
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
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

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
    public List<DetailSnsReviewResponse> findMappingReviewScrappedByUser(User user, Pageable pageable) {
        // 유저가 스크랩한 리뷰를 모두 매핑해서 가져와야 한다.
        BooleanExpression scrappedByUser = reviewScrap.user.eq(user);
        QBean<ReviewMappingDTO> bean = getReviewMappingDTOQBean(reviewScrap.review);

        Supplier<JPAQuery<ReviewMappingDTO>> codeSupplier = () -> jpaQueryFactory.select(bean)
                .from(review,reviewScrap, fileInfo, fileManager)
                .leftJoin(reaction).on(review.eq(reaction.review))
                .leftJoin(reviewScrap).on(reviewScrap.review.eq(review).and(scrappedByUser))
                .where(fileExpressionWithInsertion(scrappedByUser, reviewScrap.review));

        return findMappingReview(codeSupplier, user, pageable, reviewScrap.review);
    }

    @Override
    public List<DetailSnsReviewResponse> findMappingReviewById(User user,Long reviewId, Pageable pageable) {
        QBean<ReviewMappingDTO> bean = getReviewMappingDTOQBean(review);
        BooleanExpression lessThanReviewId = getExpressionOfId(reviewId);
        BooleanExpression userOnReviewScrap = getReviewScrapOfUser(user);

        Supplier<JPAQuery<ReviewMappingDTO>> codeSupplier = () -> jpaQueryFactory.select(bean)
                .from(review, fileInfo, fileManager)
                .leftJoin(reaction).on(review.eq(reaction.review))
                .leftJoin(reviewScrap).on(reviewScrap.review.eq(review).and(userOnReviewScrap))
                .where(fileExpressionWithInsertion(lessThanReviewId,review));

        return findMappingReview(codeSupplier, user, pageable, review);
    }

    @Override
    public List<DetailSnsReviewResponse> findMappingReviewByProductNameLikeOrContentLike(String searchKey, User reviewSearcher, Pageable pageable) {
        QBean<ReviewMappingDTO> bean = getReviewMappingDTOQBean(review);
        BooleanExpression productNameLikeOrContentLike = getExpressionOfProductNameLikeOrContentLike(searchKey);

        Supplier<JPAQuery<ReviewMappingDTO>> codeSupplier = () -> jpaQueryFactory.select(bean)
                .from(review, fileInfo, fileManager)
                .leftJoin(reaction).on(review.eq(reaction.review))
                .leftJoin(reviewScrap).on(reviewScrap.review.eq(review).and(getReviewScrapOfUser(reviewSearcher)))
                .where(fileExpressionWithInsertion(productNameLikeOrContentLike,review));

        return findMappingReview(codeSupplier, reviewSearcher, pageable,review);
    }

    public List<DetailSnsReviewResponse> findMappingReview(Supplier<JPAQuery<ReviewMappingDTO>> codeSupply,
                                                           User reviewSearcher,
                                                           Pageable pageable,
                                                           QReview reviewEntity){

        Map<Review, Group> transform = codeSupply.get()
                .orderBy(reviewEntity.reviewId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .transform(
                        groupBy(reviewEntity).as(
                                list(fileInfo.realFilename),
                                list(reaction),
                                set(reviewScrap)));

        return getDetailSnsReviewResponses(reviewSearcher, transform);
    }

    private BooleanExpression fileExpressionWithInsertion(BooleanExpression expression, QReview reviewEntity) {
        return expression
                .and(fileInfo.eq(fileManager.fileInfo))
                .and(fileInfo.exist.isTrue())
                .and(fileManager.referenceId.eq(reviewEntity.reviewId)
                        .and(fileManager.referenceType.eq(ReferenceType.REVIEW)));
    }

    private QBean<ReviewMappingDTO> getReviewMappingDTOQBean(QReview reviewEntity) {
        return Projections.bean(
                ReviewMappingDTO.class,
                reviewEntity.as("REVIEW"),
                list(fileInfo.realFilename).as("reviewImageNameList"),
                list(reaction).as("reactionList"),
                set(reviewScrap).as("reviewScrap")
        );
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

    private BooleanExpression getExpressionOfId(Long reviewId) {
        if(reviewId == null){
            return review.reviewId.isNotNull();
        }
        return review.reviewId.lt(reviewId);
    }
    private BooleanExpression getExpressionOfProductNameLikeOrContentLike(String searchKey) {
        return review.productName.contains(searchKey).or(review.content.contains(searchKey));
    }
}