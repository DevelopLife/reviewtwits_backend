package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.annotation.project.ChartPeriod;
import com.developlife.reviewtwits.message.request.StatMessageRequest;
import com.developlife.reviewtwits.message.response.statistics.DailyVisitInfoResponse;
import com.developlife.reviewtwits.message.response.project.RecentVisitInfoResponse;
import com.developlife.reviewtwits.message.response.statistics.VisitTotalGraphResponse;
import com.developlife.reviewtwits.message.response.project.*;
import com.developlife.reviewtwits.message.response.statistics.SaveStatResponse;
import com.developlife.reviewtwits.message.response.statistics.SimpleProjectInfoResponse;
import com.developlife.reviewtwits.service.StatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-04-23
 */

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Validated
public class StatController {

    private final StatService statService;

    @PostMapping("/visited-info")
    public SaveStatResponse saveVisitedInfo(@AuthenticationPrincipal User user,
                                            @RequestBody @Valid StatMessageRequest statMessageRequest){

        return statService.saveStatInfo(user,statMessageRequest);
    }

    @GetMapping("/visit-graph-infos")
    public VisitTotalGraphResponse getVisitGraphInfos(@AuthenticationPrincipal User user,
                                                      @RequestParam
                                                      @Min(value = 1, message = "프로젝트 아이디는 1 이상의 수로 입력해야 합니다.") Long projectId,
                                                      @RequestParam @ChartPeriod String range,
                                                      @RequestParam @ChartPeriod String interval){

        return statService.getVisitGraphInfos(projectId, range, interval, user);
    }
    @GetMapping("/daily-visit-graph-infos")
    public DailyVisitInfoResponse getDailyVisitInfos(@AuthenticationPrincipal User user,
                                                     @RequestParam
                                                     @Min(value = 1, message = "프로젝트 아이디는 1 이상의 수로 입력해야 합니다.") Long projectId,
                                                     @RequestParam @ChartPeriod String range){
        return statService.getDailyVisitInfos(projectId, range, user);
    }
    @GetMapping("/recent-visit-counts")
    public RecentVisitInfoResponse getRecentVisitCounts(@AuthenticationPrincipal User user,
                                                        @RequestParam @Min(value = 1, message = "프로젝트 아이디는 1 이상의 수로 입력해야 합니다.") Long projectId
    ){
        return statService.getRecentVisitCounts(projectId, user);
    }

    @GetMapping("/dashboard/simple-project-info")
    public SimpleProjectInfoResponse dashBoardSimpleInfo(@AuthenticationPrincipal User user,
                                                         @RequestParam @Min(value = 1, message = "프로젝트 아이디는 1 이상의 수로 입력해야 합니다.") Long projectId) {
        return statService.getSimpleProjectInfo(projectId, user);
    }

    @GetMapping("/dashboard/product-statistics")
    public List<ProductStatisticsResponse> dashBoardProductStatisticsInfo(@AuthenticationPrincipal User user,
                                                                          @RequestParam @Min(value = 1, message = "프로젝트 아이디는 1 이상의 수로 입력해야 합니다.") Long projectId) {
        return statService.getProductStatisticsInfo(projectId, user);
    }

    @GetMapping("/request-inflow-infos")
    public SearchFlowResponse getRequestSearchFlowInfos(@AuthenticationPrincipal User user,
                                                    @RequestParam @Min(value = 1, message = "프로젝트 아이디는 1 이상의 수로 입력해야 합니다.") Long projectId) {
        return statService.getRequestSearchFlowInfos(projectId, user);
    }
}