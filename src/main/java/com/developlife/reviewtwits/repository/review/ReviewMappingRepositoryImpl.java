package com.developlife.reviewtwits.repository.review;

import com.developlife.reviewtwits.entity.QReview;
import com.developlife.reviewtwits.entity.Reaction;
import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.mapper.ReviewMapper;
import com.developlife.reviewtwits.mapper.UserMapper;
import com.developlife.reviewtwits.message.response.review.DetailShoppingMallReviewResponse;
import com.developlife.reviewtwits.message.response.review.ReactionResponse;
import com.developlife.reviewtwits.message.response.sns.DetailSnsReviewResponse;
import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import com.developlife.reviewtwits.type.ReactionType;
import com.developlife.reviewtwits.type.ReferenceType;
import com.developlife.reviewtwits.type.review.ReviewStatus;
import com.querydsl.core.group.Group;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

import static com.developlife.reviewtwits.entity.QFileInfo.fileInfo;
import static com.developlife.reviewtwits.entity.QFileManager.fileManager;
import static com.developlife.reviewtwits.entity.QFollow.follow;
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
    private final UserMapper userMapper;

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
        BooleanExpression lessThanReviewId = getExpressionOfId(reviewId, pageable);

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

    @Override
    public List<UserInfoResponse> findRecentUpdateUsers(User requestedUser, Pageable pageable) {
        // 팔로우한 사람들 중, 가장 최근에 리뷰를 업데이트 한 사람 n 명을 구하기
        if(requestedUser == null){
            return new ArrayList<>();
        }

        List<User> foundUser = jpaQueryFactory.select(review.user).from(review)
                .where(review.user.in(
                        JPAExpressions.select(follow.targetUser).from(follow)
                                .where(follow.user.eq(requestedUser))
                ))
                .groupBy(review.user)
                .orderBy(review.reviewId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<UserInfoResponse> userInfoResponseList = new ArrayList<>();
        for(User user : foundUser){
            userInfoResponseList.add(userMapper.toUserInfoResponse(user,0,0,0,true));
        }
        return userInfoResponseList;
    }

    @Override
    public List<DetailShoppingMallReviewResponse> findReviewsBySearchInfo(User user, Long reviewId, String status, String startDate,
                                                                          String endDate, String keyword, Pageable pageable) {
        BooleanExpression expression = makeSearchExpression(user, status, startDate, endDate, keyword);
        BooleanExpression lessThanReviewId = getExpressionOfId(reviewId, pageable);

        Pageable realPageable = getPageableContainsImageCount(pageable, expression.and(lessThanReviewId));

        OrderSpecifier<Long> orders;
        if(pageable.getSort().equals(Sort.by("reviewId").descending())){
            orders = review.reviewId.desc();
        }else{
            orders = review.reviewId.asc();
        }

        Map<Review, List<String>> transform = jpaQueryFactory.select(review, fileInfo.realFilename)
                .from(review, fileInfo, fileManager)
                .where(fileExpressionWithInsertion(expression.and(lessThanReviewId), review))
                .orderBy(orders)
                .offset(realPageable.getOffset())
                .limit(realPageable.getPageSize())
                .transform(
                        groupBy(review).as(
                                list(fileInfo.realFilename)));

        List<DetailShoppingMallReviewResponse> resultList = new ArrayList<>();
        for(Map.Entry<Review, List<String>> entry : transform.entrySet()){
            Review targetReview = entry.getKey();
            targetReview.setReviewImageUuidList(entry.getValue());
            resultList.add(reviewMapper.mapReviewToDetailReviewResponse(targetReview));
        }
        return resultList;
    }

    @Override
    public List<DetailShoppingMallReviewResponse> findReviewListMappingInfoByProductURL(User user, String productURL, String sort) {
        BooleanExpression findByProductUrl = review.productUrl.eq(productURL).and(review.project.isNotNull());

        Map<Review, Group> transform = jpaQueryFactory.select(review, fileInfo.realFilename, reaction)
                .from(review, fileInfo, fileManager)
                .leftJoin(reaction).on(review.eq(reaction.review))
                .where(fileExpressionWithInsertion(findByProductUrl, review))
                .orderBy(review.reviewId.desc())
                .transform(
                        groupBy(review).as(
                                list(fileInfo.realFilename),
                                set(reaction)));

        List<DetailShoppingMallReviewResponse> resultList = new ArrayList<>();
        for(Map.Entry<Review, Group> entry : transform.entrySet()){
            Review targetReview = entry.getKey();
            targetReview.setReviewImageUuidList(entry.getValue().getList(fileInfo.realFilename));
            boolean isLiked = isThisUserLikeThisReview(user, entry.getValue().getSet(reaction));
            targetReview.setLiked(isLiked);
            resultList.add(reviewMapper.mapReviewToDetailReviewResponse(targetReview));
        }

        if(sort.equals("BEST")) {
            resultList.sort(Comparator.comparing(DetailShoppingMallReviewResponse::score).reversed());
        }

        return resultList;
    }

    private boolean isThisUserLikeThisReview(User user, Set<Reaction> reactionSet){
        for (Reaction reaction : reactionSet) {
            if(reaction.getUser().equals(user)){
                return true;
            }
        }
        return false;
    }


    private BooleanExpression makeSearchExpression(User user,String status, String startDate, String endDate, String keyword){
        BooleanExpression expression = review.project.user.eq(user);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if(startDate != null){
            LocalDateTime startLocalDate = LocalDate.parse(startDate, formatter).atStartOfDay();
            expression = expression.and(review.lastModifiedDate.after(startLocalDate));
        }

        if(endDate != null){
            LocalDateTime endLocalDate = LocalDate.parse(endDate, formatter).atTime(LocalTime.MAX);
            expression = expression.and(review.lastModifiedDate.before(endLocalDate));
        }

        if(status != null){
            expression = expression.and(review.status.eq(ReviewStatus.valueOf(status)));
        }

        if(keyword != null){
            List<String> keywordList = Arrays.stream(keyword.split("\\+")).toList();
            for(String key : keywordList){
                expression = expression.and(review.content.contains(key));
            }
        }

        return expression;
    }

    private Pageable getPageableContainsImageCount(Pageable pageable, BooleanExpression expression){
        List<Review> reviewList = jpaQueryFactory.selectFrom(review)
                .where(expression)
                .orderBy(review.reviewId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        int realPageableSize = 0;
        for(Review review : reviewList){
            realPageableSize += getReviewFieldCount(review.getReviewImageCount());
        }

        if(realPageableSize == 0){
            realPageableSize = 1;
        }

        return PageRequest.of(0,realPageableSize, pageable.getSort());
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

    private BooleanExpression getExpressionOfId(Long reviewId, Pageable pageable) {
        if(reviewId == null){
            return review.reviewId.isNotNull();
        }

        if(pageable.getSort().equals(Sort.by("reviewId").descending())){
            return review.reviewId.lt(reviewId);
        }
        return review.reviewId.gt(reviewId);
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