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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

        Pageable realPageable = getRealPageable(pageable, scrappedByUser, reviewScrap.review, reviewScrap);

        Supplier<JPAQuery<ReviewMappingDTO>> codeSupplier = () -> jpaQueryFactory.select(bean)
                .from(review,reviewScrap, fileInfo, fileManager)
                .leftJoin(reaction).on(review.eq(reaction.review))
                .leftJoin(reviewScrap).on(reviewScrap.review.eq(review).and(scrappedByUser))
                .where(fileExpressionWithInsertion(scrappedByUser, reviewScrap.review));

        return findMappingReview(codeSupplier, user, realPageable, reviewScrap.review);
    }

    @Override
    public List<DetailSnsReviewResponse> findMappingReviewById(User user,Long reviewId, Pageable pageable) {
        QBean<ReviewMappingDTO> bean = getReviewMappingDTOQBean(review);
        BooleanExpression lessThanReviewId = getExpressionOfId(reviewId);

        Pageable realPageable = getRealPageable(pageable, lessThanReviewId, review, review);

        Supplier<JPAQuery<ReviewMappingDTO>> codeSupplier = () -> jpaQueryFactory.select(bean)
                .from(review, fileInfo, fileManager)
                .leftJoin(reaction).on(review.eq(reaction.review))
                .leftJoin(reviewScrap).on(reviewScrap.review.eq(review).and(getReviewScrapOfUser(user)))
                .where(fileExpressionWithInsertion(lessThanReviewId,review));

        return findMappingReview(codeSupplier, user, realPageable, review);
    }

    @Override
    public List<DetailSnsReviewResponse> findMappingReviewByProductNameLikeOrContentLike(String searchKey, User reviewSearcher, Pageable pageable) {
        QBean<ReviewMappingDTO> bean = getReviewMappingDTOQBean(review);
        BooleanExpression productNameLikeOrContentLike = getExpressionOfProductNameLikeOrContentLike(searchKey);

        Pageable realPageable = getRealPageable(pageable, productNameLikeOrContentLike, review, review);

        Supplier<JPAQuery<ReviewMappingDTO>> codeSupplier = () -> jpaQueryFactory.select(bean)
                .from(review, fileInfo, fileManager)
                .leftJoin(reaction).on(review.eq(reaction.review))
                .leftJoin(reviewScrap).on(reviewScrap.review.eq(review).and(getReviewScrapOfUser(reviewSearcher)))
                .where(fileExpressionWithInsertion(productNameLikeOrContentLike,review));

        return findMappingReview(codeSupplier, reviewSearcher, realPageable, review);
    }

    @Override
    public DetailSnsReviewResponse findOneMappingReviewById(User reviewSearcher, long reviewId) {
        BooleanExpression findByReviewId = review.reviewId.eq(reviewId);
        QBean<ReviewMappingDTO> bean = getReviewMappingDTOQBean(review);

        Supplier<JPAQuery<ReviewMappingDTO>> codeSupplier = () -> jpaQueryFactory.select(bean)
                .from(review,fileInfo, fileManager)
                .leftJoin(reaction).on(review.eq(reaction.review))
                .leftJoin(reviewScrap).on(reviewScrap.review.eq(review).and(getReviewScrapOfUser(reviewSearcher))) // 리뷰 스크랩은 하나만 있으면 된다. 그러므로, 유저 정보도 같이 걸어서 넣어 주는 것이 좋다.
                .where(fileExpressionWithInsertion(findByReviewId,review));

        PageRequest pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("reviewId").descending());

        List<DetailSnsReviewResponse> resultReview = findMappingReview(codeSupplier, reviewSearcher, pageable ,review);
        if(resultReview.isEmpty()){
            return null;
        }
        return resultReview.get(0);
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
                                sortedSet(fileInfo.realFilename),
                                set(reaction),
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
                sortedSet(fileInfo.realFilename).as("reviewImageNameList"),
                set(reaction).as("reactionList"),
                set(reviewScrap).as("reviewScrap")
        );
    }

    private List<DetailSnsReviewResponse> getDetailSnsReviewResponses(User reviewSearcher, Map<Review, Group> transform) {
        List<ReviewMappingDTO> reviewMappingList = transform.entrySet().stream()
                .map(entry -> new ReviewMappingDTO(
                        entry.getKey(),
                        entry.getValue().getSortedSet(fileInfo.realFilename),
                        entry.getValue().getSet(reaction),
                        entry.getValue().getSet(reviewScrap))).toList();

        List<DetailSnsReviewResponse> snsResponse = new ArrayList<>();

        for(ReviewMappingDTO reviewMappingDTO : reviewMappingList){
            Review review = reviewMappingDTO.getReview();
            review.setReviewImageUuidList(reviewMappingDTO.getReviewImageNameSet().stream().toList());

            Map<String, ReactionResponse> collectedReactionResponse = ReactionType.classifyReactionResponses(
                    reviewSearcher,
                    reviewMappingDTO.getReactionSet().stream().toList()
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

    private Pageable getRealPageable(Pageable pageable, BooleanExpression expression, QReview reviewEntity, EntityPathBase<?> pathBase){
        List<Review> reviewList = jpaQueryFactory.select(reviewEntity).from(pathBase)
                .where(expression)
                .orderBy(reviewEntity.reviewId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        int realPageableSize = 0;

        for(Review review : reviewList){
            int reactionCount = getReviewFieldCount(review.getReactionCount());
            int reviewImageCount = getReviewFieldCount(review.getReviewImageCount());
            realPageableSize += (reactionCount * reviewImageCount);
        }

        if(realPageableSize == 0){
            realPageableSize = 1;
        }

        return PageRequest.of(0,realPageableSize,Sort.by("reviewId").descending());
    }

    private int getReviewFieldCount(int count){
        if(count == 0){
            return 1;
        }
        return count;
    }
}