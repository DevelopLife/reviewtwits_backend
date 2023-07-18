package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.annotation.common.DateFormat;
import com.developlife.reviewtwits.message.annotation.project.ChartPeriod;
import com.developlife.reviewtwits.message.annotation.project.ProjectName;
import com.developlife.reviewtwits.message.response.statistics.DailyVisitInfoResponse;
import com.developlife.reviewtwits.message.response.statistics.VisitTotalGraphResponse;
import com.developlife.reviewtwits.service.StatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;

/**
 * @author WhalesBob
 * @since 2023-07-18
 */
@RestController
@RequestMapping("/statistics/tick-counts")
@RequiredArgsConstructor
@Validated
public class StatTickController {

    private final StatService statService;

    @GetMapping("/visit-graph-infos")
    public VisitTotalGraphResponse getVisitGraphInfos(@AuthenticationPrincipal User user,
                                                      @RequestParam
                                                      @ProjectName String projectName,
                                                      @RequestParam @ChartPeriod String interval,
                                                      @RequestParam @Min(value = 1, message = "통계에서 받을 정보의 갯수는 1개 이상이어야 합니다.")
                                                      Integer count,
                                                      @RequestParam(required = false) @DateFormat String endDate){
        return statService.getVisitGraphInfos(projectName, count, interval, user, endDate );
    }



}