package com.developlife.reviewtwits.repository.project;

import com.developlife.reviewtwits.entity.*;
import com.developlife.reviewtwits.message.response.statistics.SimpleProjectInfoResponse;
import com.developlife.reviewtwits.type.review.ReviewStatus;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * @author ghdic
 * @since 2023/06/09
 */
@Repository
public class StatInfoCustomRepositoryImpl implements StatInfoCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public StatInfoCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public SimpleProjectInfoResponse findSimpleProjectInfo(Project project) {
        LocalDate currentDate = LocalDate.now();

        // 월간 조회수
        Long monthlyVisitCount = jpaQueryFactory.select(QStatInfo.statInfo.statId.count())
                .from(QStatInfo.statInfo)
                .where(QStatInfo.statInfo.project.eq(project))
                .where(QStatInfo.statInfo.createdDate.month().eq(currentDate.getMonthValue())
                    .and(QStatInfo.statInfo.createdDate.year().eq(currentDate.getYear())))
                .fetchOne();
        // 일간 리뷰수
        Long dailyReviewCount = jpaQueryFactory.select(QReview.review.reviewId.count())
                .from(QReview.review)
                .where(QReview.review.project.eq(project))
                .where(QReview.review.createdDate.dayOfMonth().eq(currentDate.getDayOfMonth())
                    .and(QReview.review.createdDate.month().eq(currentDate.getMonthValue())
                    .and(QReview.review.createdDate.year().eq(currentDate.getYear()))))
                .fetchOne();
        // 대기중인 리뷰수
        Long pendingReviewCount = jpaQueryFactory.select(QReview.review.reviewId.count())
                .from(QReview.review)
                .where(QReview.review.project.eq(project))
                .where(QReview.review.status.eq(ReviewStatus.PENDING))
                .fetchOne();
        // 등록된 상품수
        Long registeredProductCount = jpaQueryFactory.select(QProduct.product.productId.count())
                .from(QProduct.product)
                .where(QProduct.product.project.eq(project))
                .fetchOne();

        return SimpleProjectInfoResponse.builder()
                .monthlyVisitCount(monthlyVisitCount)
                .dailyReviewCount(dailyReviewCount)
                .pendingReviewCount(pendingReviewCount)
                .registeredProductCount(registeredProductCount)
                .build();
    }
}
