package com.developlife.reviewtwits.repository.project;

import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.StatInfo;
import com.developlife.reviewtwits.message.response.project.RecentVisitInfoResponse;
import com.developlife.reviewtwits.message.response.project.VisitInfoResponse;
import com.developlife.reviewtwits.type.project.ChartPeriodUnit;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
    public VisitInfoResponse findByPeriod(Project project, ChartPeriodUnit range, ChartPeriodUnit interval) {

        Map<Integer, List<StatInfo>> visitStatInfo = getVisitStatInfo(project, range, interval);
        return mappingVisitInfoResponse(visitStatInfo);
    }

    @Override
    public RecentVisitInfoResponse findRecentVisitInfo(Project project) {

        Map<Integer, List<StatInfo>> visitStatInfo = getVisitStatInfo(project, ChartPeriodUnit.FIVE_YEAR, ChartPeriodUnit.ONE_DAY);

        int today = LocalDateTime.now().getDayOfYear();
        int yesterday = LocalDateTime.now().minusDays(1).getDayOfYear();

        int todayVisitCount = visitStatInfo.get(today).size();
        int yesterdayVisitCount = visitStatInfo.get(yesterday).size();
        int totalVisitCount = 0;

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
                    .and(statInfo.createdDate.after(ChartPeriodUnit.getTimeRangeBefore(range))))
                .transform(groupBy(intervalExpression).as(list(statInfo)));
    }

    private VisitInfoResponse mappingVisitInfoResponse(Map<Integer, List<StatInfo>> transform){

        List<String> timeStamp = new ArrayList<>();
        List<Integer> visitCountList = new ArrayList<>();
        List<Integer> previousCompareList = new ArrayList<>();

        Map<Integer, List<StatInfo>> sortedMap = new TreeMap<>(transform);

        for(Map.Entry<Integer, List<StatInfo>> entry : sortedMap.entrySet()){
            String date = entry.getValue().get(0).getCreatedDate().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            int visitCount = entry.getValue().size();

            timeStamp.add(date);
            visitCountList.add(visitCount);

            if(previousCompareList.isEmpty()){
                previousCompareList.add(0);
                continue;
            }
            previousCompareList.add(visitCount - visitCountList.get(visitCountList.size() - 2));
        }

        return VisitInfoResponse.builder()
                .timeStamp(timeStamp)
                .visitCount(visitCountList)
                .previousCompare(previousCompareList)
                .build();
    }
}