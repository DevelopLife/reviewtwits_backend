package com.developlife.reviewtwits.repository.project;

import com.developlife.reviewtwits.entity.*;
import com.developlife.reviewtwits.message.response.project.SearchFlowResponse;
import com.developlife.reviewtwits.message.response.statistics.SimpleProjectInfoResponse;
import com.developlife.reviewtwits.type.review.ReviewStatus;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Override
    public SearchFlowResponse findSearchFlow(Project project) {
        List<StatInfo> statInfoList = jpaQueryFactory.selectFrom(QStatInfo.statInfo)
                .where(QStatInfo.statInfo.project.eq(project))
                .fetch();
        // Long total, naver, daum, google, zoom, bing, yahoo, etc;
        Map<String, Long> result = new HashMap<>();
        String[] sites = {"google", "naver", "daum", "zoom", "bing", "yahoo"};

        // 초기화
        for (String site : sites) {
            result.put(site, 0L);
        }
        result.put("etc", 0L);

        // 카운트 증가
        for (StatInfo si : statInfoList) {
            String referer = si.getInflowUrl();
            boolean matched = false;

            for (String site : sites) {
                if (referer.contains(site)) {
                    result.put(site, result.get(site) + 1);
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                result.put("etc", result.get("etc") + 1);
            }
        }


        return SearchFlowResponse.builder()
                .total(result.values().stream().mapToLong(Long::longValue).sum())
                .google(result.get("google"))
                .naver(result.get("naver"))
                .daum(result.get("daum"))
                .zoom(result.get("zoom"))
                .bing(result.get("bing"))
                .yahoo(result.get("yahoo"))
                .etc(result.get("etc"))
                .build();
    }
}
