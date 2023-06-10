package com.developlife.reviewtwits.repository.statistics;

import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.message.response.project.ProductStatisticsResponse;
import com.developlife.reviewtwits.message.response.project.RecentVisitInfoResponse;
import com.developlife.reviewtwits.message.response.statistics.VisitInfoResponse;
import com.developlife.reviewtwits.type.ChartPeriodUnit;

import java.util.List;
import java.util.Map;


public interface PeriodCheckingRepository {
    List<VisitInfoResponse> findByPeriod(Project project, ChartPeriodUnit range, ChartPeriodUnit interval);
    RecentVisitInfoResponse findRecentVisitInfo(Project project);
    Map<Integer, Long> readTimeGraphInfo(Project project);

    List<ProductStatisticsResponse> findProductStatistics(Project project);
}
