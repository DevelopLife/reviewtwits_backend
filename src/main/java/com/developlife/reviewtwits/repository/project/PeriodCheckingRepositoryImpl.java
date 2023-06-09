package com.developlife.reviewtwits.repository.project;

import com.developlife.reviewtwits.entity.Product;
import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.StatInfo;
import com.developlife.reviewtwits.message.response.project.ProductStatisticsResponse;
import com.developlife.reviewtwits.message.response.project.RecentVisitInfoResponse;
import com.developlife.reviewtwits.message.response.project.VisitInfoResponse;
import com.developlife.reviewtwits.type.project.ChartPeriodUnit;
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
    public List<VisitInfoResponse> findByPeriod(Project project, ChartPeriodUnit range, ChartPeriodUnit interval) {

        Map<Integer, List<StatInfo>> visitStatInfo = getVisitStatInfo(project, range, interval);
        return mappingVisitInfoResponse(visitStatInfo, interval);
    }

    @Override
    public RecentVisitInfoResponse findRecentVisitInfo(Project project) {

        Map<Integer, List<StatInfo>> visitStatInfo = getVisitStatInfo(project, ChartPeriodUnit.FIVE_YEAR, ChartPeriodUnit.ONE_DAY);

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
                .select(statInfo)
                .where(statInfo.project.eq(project))
                .transform(groupBy(statInfo.product).as(list(statInfo)));
         List<ProductStatisticsResponse> response = new ArrayList<>();
//         result.forEach((product, statInfos) -> {
//             ProductStatisticsResponse p = ProductStatisticsResponse.builder()
//                     .productName(product.getProductName())
//                     .visitCount(statInfos.size())
//                     .reviewCount()
//                     .mainAge(statInfos.stream().map(s -> s.getUser().getAge() / 10)
//                             .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
//                             .values().stream()
//                             .max(Comparator.comparing(a -> a))
//                             .get() * 10
//                     )
//                     .mainGender(statInfos.stream().map(s -> s.getUser().getGender())
//                             .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
//                             .values().stream()
//                             .max(Comparator.comparing(a -> a))
//                             .get()
//                     )
//                     .averageScore()
//                     .build();
//                response.add(p);
//         });


        return response;
    }

    private Map<Integer, List<StatInfo>> getVisitStatInfo(Project project, ChartPeriodUnit range, ChartPeriodUnit interval) {
        NumberExpression<Integer> intervalExpression = ChartPeriodUnit.getExpressionOfInterval(interval);

        return jpaQueryFactory.select(
                        statInfo.createdDate.dayOfMonth(),
                        statInfo.createdDate.month(),
                        statInfo.createdDate.year()
                ).from(statInfo)
                .where(statInfo.project.eq(project)
                    .and(statInfo.createdDate.after(ChartPeriodUnit.getTimeRangeBefore(LocalDateTime.now(),range))))
                .transform(groupBy(intervalExpression).as(list(statInfo)));
    }

    private List<VisitInfoResponse> mappingVisitInfoResponse(Map<Integer, List<StatInfo>> transform, ChartPeriodUnit interval){

        Map<Integer, List<StatInfo>> sortedMap = new TreeMap<>(transform);
        List<VisitInfoResponse> response = new ArrayList<>();

        for(Map.Entry<Integer, List<StatInfo>> entry : sortedMap.entrySet()){
            String date = entry.getValue().get(0).getCreatedDate().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            int visitCount = entry.getValue().size();
            int previousCompare = 0;

            if(!response.isEmpty()){
                VisitInfoResponse previousDate = response.get(response.size() - 1);
                if (isDifferenceValid(previousDate.timeStamp(),date, interval)){
                    previousCompare = visitCount - previousDate.visitCount();
                }else{
                    previousCompare = visitCount;
                }
            }
            response.add(VisitInfoResponse.builder()
                    .timeStamp(date)
                    .visitCount(visitCount)
                    .previousCompare(previousCompare)
                    .build());
        }
        return response;
    }
    private boolean isDifferenceValid(String previousDate, String presentDate, ChartPeriodUnit interval){
        LocalDate present = LocalDate.parse(presentDate);
        LocalDate previous = LocalDate.parse(previousDate);
        LocalDate toCompareDate = ChartPeriodUnit.getTimeRangeBefore(present.atTime(LocalTime.now()),interval).toLocalDate();
        return toCompareDate.isEqual(previous);
    }
}