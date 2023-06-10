package com.developlife.reviewtwits.repository.project;

import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.StatInfo;
import com.developlife.reviewtwits.message.response.project.RecentVisitInfoResponse;
import com.developlife.reviewtwits.message.response.statistics.VisitInfoResponse;
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
        LocalDate startDate = ChartPeriodUnit.getTimeRangeBefore(LocalDateTime.now(), range, interval).toLocalDate();
        Map<Integer, List<StatInfo>> visitStatInfo = getVisitStatInfo(project, startDate, LocalDate.now(), interval);
        return mappingVisitInfoResponse(visitStatInfo, interval, startDate, LocalDate.now());
    }

    @Override
    public RecentVisitInfoResponse findRecentVisitInfo(Project project) {
        LocalDate startDate = ChartPeriodUnit.getTimeRangeBefore(LocalDateTime.now(), ChartPeriodUnit.FIVE_YEAR, ChartPeriodUnit.ONE_DAY).toLocalDate();

        Map<Integer, List<StatInfo>> visitStatInfo = getVisitStatInfo(project, startDate, LocalDate.now(), ChartPeriodUnit.ONE_DAY);

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

    private Map<Integer, List<StatInfo>> getVisitStatInfo(Project project, LocalDate startDate, LocalDate endDate, ChartPeriodUnit interval) {
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
    private boolean isDifferenceValid(String previousDate, String presentDate, ChartPeriodUnit interval){
        LocalDate present = LocalDate.parse(presentDate);
        LocalDate previous = LocalDate.parse(previousDate);
        LocalDate toCompareDate = ChartPeriodUnit.getTimeRangeBefore(present.atTime(LocalTime.now()),ChartPeriodUnit.FIVE_YEAR,interval).toLocalDate();
        return toCompareDate.isEqual(previous);
    }

    private List<VisitInfoResponse> makeVisitInfoResponseInit(LocalDate startDate, LocalDate endDate, ChartPeriodUnit interval){
        List<VisitInfoResponse> initList = new ArrayList<>();
        LocalDate date = startDate;
        while(date.isBefore(endDate)){
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