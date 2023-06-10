package com.developlife.reviewtwits.repository.project;

import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.message.response.project.RecentVisitInfoResponse;
import com.developlife.reviewtwits.message.response.statistics.VisitInfoResponse;
import com.developlife.reviewtwits.type.project.ChartPeriodUnit;

import java.util.List;

public interface PeriodCheckingRepository {
    List<VisitInfoResponse> findByPeriod(Project project, ChartPeriodUnit range, ChartPeriodUnit interval);
    RecentVisitInfoResponse findRecentVisitInfo(Project project);
}
