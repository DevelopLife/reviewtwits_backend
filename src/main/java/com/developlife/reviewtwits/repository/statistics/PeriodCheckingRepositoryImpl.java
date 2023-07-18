package com.developlife.reviewtwits.repository.statistics;

import com.developlife.reviewtwits.entity.Product;
import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.QReview;
import com.developlife.reviewtwits.entity.StatInfo;
import com.developlife.reviewtwits.message.response.project.ProductStatisticsResponse;
import com.developlife.reviewtwits.message.response.project.RecentVisitInfoResponse;
import com.developlife.reviewtwits.message.response.statistics.VisitInfoResponse;
import com.developlife.reviewtwits.type.ChartPeriodUnit;
import com.developlife.reviewtwits.type.Gender;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.developlife.reviewtwits.entity.QStatInfo.statInfo;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

/**
 * @author WhalesBob
 * @since 2023-04-22
 */
@Repository
public class PeriodCheckingRepositoryImpl implements PeriodCheckingRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public PeriodCheckingRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<VisitInfoResponse> findByPeriod(Project project, LocalDate endDate, LocalDate startDate, ChartPeriodUnit interval) {
        Map<Integer, List<StatInfo>> visitStatInfo = getVisitStatInfo(project, startDate, endDate);
        return mappingVisitInfoResponse(visitStatInfo, interval, startDate, endDate);
    }

    @Override
    public RecentVisitInfoResponse findRecentVisitInfo(Project project) {
        LocalDate startDate = ChartPeriodUnit.getTimeRangeBefore(LocalDateTime.now(), ChartPeriodUnit.FIVE_YEAR, ChartPeriodUnit.ONE_DAY).toLocalDate();

        Map<Integer, List<StatInfo>> visitStatInfo = getVisitStatInfo(project, startDate, LocalDate.now());

        int today = LocalDateTime.now().getDayOfYear();
        int yesterday = LocalDateTime.now().minusDays(1).getDayOfYear();

        int todayVisitCount = 0;
        int yesterdayVisitCount = 0;
        int totalVisitCount = 0;

        if(visitStatInfo.get(today) != null){
            todayVisitCount = visitStatInfo.get(today).size();
        }

        if(visitStatInfo.get(yesterday) != null){
            yesterdayVisitCount = visitStatInfo.get(yesterday).size();
        }

        for(Map.Entry<Integer, List<StatInfo>> entry : visitStatInfo.entrySet()){
            totalVisitCount += entry.getValue().size();
        }
        return RecentVisitInfoResponse.builder()
                .todayVisit(todayVisitCount)
                .yesterdayVisit(yesterdayVisitCount)
                .totalVisit(totalVisitCount)
                .build();
    }

    @Override
    public Map<Integer, Long> readTimeGraphInfo(Project project) {
        Map<Integer, Long> result = jpaQueryFactory.select(
                statInfo.createdDate.hour(), statInfo.createdDate.count()
        ).from(statInfo)
                .where(statInfo.project.eq(project))
                        //.and(statInfo.createdDate.after(ChartPeriodUnit.getTimeRangeBefore(ChartPeriodUnit.ONE_MONTH))))
                .groupBy(statInfo.createdDate.hour())
                .orderBy(statInfo.createdDate.hour().asc())
                .transform(groupBy(statInfo.createdDate.hour()).as(statInfo.createdDate.count()));

        IntStream.range(0, 24)
                .forEach(h -> {
                    if (!result.containsKey(h)) {
                        result.put(h, 0L);
                    }
                });
        return new TreeMap<>(result);
    }
    // 맵으로 product key로해서 visitCount구하고
    @Override
    public List<ProductStatisticsResponse> findProductStatistics(Project project) {
         Map<Product, List<StatInfo>> result = jpaQueryFactory
                .selectFrom(statInfo)
                .where(statInfo.project.eq(project))
                .transform(groupBy(statInfo.product).as(list(statInfo)));
         List<ProductStatisticsResponse> response = new ArrayList<>();
         result.forEach((product, statInfos) -> {
             Collection<Long> mainAgeValues = statInfos.stream().filter(s -> s.getUser() != null && s.getUser().getProvider() != null)
                     .map(s -> (s.getUser().getAge() / 10))
                     .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                     .values();

             Gender gender = statInfos.stream().filter(s -> s.getUser() != null
                             && s.getUser().getProvider() != null && s.getUser().getGender() != null)
                     .map(s -> s.getUser().getGender())
                     .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                     .entrySet().stream()
                     .max(Map.Entry.comparingByValue())
                     .map(Map.Entry::getKey)
                     .orElse(null);

             Tuple reviewTuple = jpaQueryFactory.select(QReview.review.count(), QReview.review.score.avg())
                        .from(QReview.review)
                        .where(QReview.review.productUrl.eq(product.getProductUrl()))
                        .fetchOne();

             Long reviewCount = reviewTuple.get(QReview.review.count());
             Double averageScore = reviewTuple.get(QReview.review.score.avg());

             ProductStatisticsResponse p = ProductStatisticsResponse.builder()
                     .productName(product.getProductName())
                     .visitCount(Long.valueOf(statInfos.size()))
                     .reviewCount(reviewCount)
                     .mainAge(mainAgeValues.size() == 0 ? null
                             : mainAgeValues.stream()
                             .max(Comparator.comparing(a -> a))
                             .get()
                     )
                     .mainGender(gender == null ? null: gender.name())
                     .averageScore(reviewCount == 0 ? 0.0 : averageScore)
                     .build();
                response.add(p);
         });


        return response;
    }

    private Map<Integer, List<StatInfo>> getVisitStatInfo(Project project, LocalDate startDate, LocalDate endDate) {

        NumberExpression<Integer> intervalExpression = statInfo.createdDate.dayOfYear();

        return jpaQueryFactory.select(
                        statInfo.createdDate.dayOfYear()
                ).from(statInfo)
                .where(statInfo.project.eq(project)
                    .and(statInfo.createdDate.after(startDate.atStartOfDay()))
                    .and(statInfo.createdDate.before(endDate.atTime(LocalTime.MAX))))
                .transform(groupBy(intervalExpression).as(list(statInfo)));
    }

    private List<VisitInfoResponse> mappingVisitInfoResponse(Map<Integer, List<StatInfo>> transform, ChartPeriodUnit interval, LocalDate startDate, LocalDate endDate){

        Map<Integer, List<StatInfo>> sortedMap = new TreeMap<>(transform);
        List<VisitInfoResponse> response = makeVisitInfoResponseInit(startDate, endDate, interval);

        int visitInfoIndex = 0;

        for(Map.Entry<Integer, List<StatInfo>> entry : sortedMap.entrySet()){
            LocalDate entryDate = entry.getValue().get(0).getCreatedDate().toLocalDate();
            while(visitInfoIndex + 1 < response.size() && isDaysBefore(response.get(visitInfoIndex+1).getTimeStamp(), entryDate)){
                visitInfoIndex++; // response 에 찍혀 있는 시간이 entry 의 시간보다 뒤에 올 때까지, response 의 index 하나씩 추가
            }

            int visitCount = entry.getValue().size();
            int currentCount = response.get(visitInfoIndex).getVisitCount();
            response.get(visitInfoIndex).setVisitCount(currentCount + visitCount);
        }

        for(int i = 1; i < response.size(); i++){
            response.get(i).setPreviousCompare(response.get(i).getVisitCount() - response.get(i-1).getVisitCount());
        }

        return response;
    }

    private List<VisitInfoResponse> makeVisitInfoResponseInit(LocalDate startDate, LocalDate endDate, ChartPeriodUnit interval){
        List<VisitInfoResponse> initList = new ArrayList<>();
        LocalDate date = startDate;
        while(date.isBefore(endDate) || date.isEqual(endDate)){
            initList.add(VisitInfoResponse.builder()
                    .timeStamp(date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                    .visitCount(0)
                    .previousCompare(0)
                    .build());
            date = ChartPeriodUnit.getTimeRangeAfter(date, interval);
        }

        return initList;
    }

    private boolean isDaysBefore(String responseTimeStamp, LocalDate compareDate){
        LocalDate responseStampDate = LocalDate.parse(responseTimeStamp);
        return responseStampDate.isBefore(compareDate) || responseStampDate.isEqual(compareDate);
    }
}