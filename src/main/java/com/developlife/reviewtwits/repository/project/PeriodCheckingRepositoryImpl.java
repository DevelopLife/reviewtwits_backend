package com.developlife.reviewtwits.repository.project;

import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.StatInfo;
import com.developlife.reviewtwits.message.response.project.RecentVisitInfoResponse;
import com.developlife.reviewtwits.message.response.project.VisitInfoResponse;
import com.developlife.reviewtwits.type.project.ChartPeriodUnit;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

    private Map<Integer, List<StatInfo>> getVisitStatInfo(Project project, ChartPeriodUnit range, ChartPeriodUnit interval) {
        NumberExpression<Integer> intervalExpression = ChartPeriodUnit.getExpressionOfInterval(interval);

        return jpaQueryFactory.select(
                        statInfo.createdDate.dayOfYear(),
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