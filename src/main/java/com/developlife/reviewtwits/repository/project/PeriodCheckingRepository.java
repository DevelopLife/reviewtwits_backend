package com.developlife.reviewtwits.repository.project;

import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.message.response.project.RecentVisitInfoResponse;
import com.developlife.reviewtwits.message.response.project.VisitInfoResponse;
import com.developlife.reviewtwits.type.project.ChartPeriodUnit;

public interface PeriodCheckingRepository {
    VisitInfoResponse findByPeriod(Project project, ChartPeriodUnit range, ChartPeriodUnit interval);
    RecentVisitInfoResponse findRecentVisitInfo(Project project);
}
