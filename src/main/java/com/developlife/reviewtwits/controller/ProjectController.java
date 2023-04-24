package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.annotation.project.ChartPeriod;
import com.developlife.reviewtwits.message.request.project.FixProjectRequest;
import com.developlife.reviewtwits.message.request.project.RegisterProjectRequest;
import com.developlife.reviewtwits.message.response.project.*;
import com.developlife.reviewtwits.service.ProjectService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * @author ghdic
 * @since 2023/03/10
 */
@RestController
@RequestMapping("/projects")
@Validated
public class ProjectController {
    ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping(value = "", produces = "application/json")
    public ProjectInfoResponse registerProject(@RequestBody @Valid RegisterProjectRequest registerProjectRequest,
                                @AuthenticationPrincipal User user) {
        return projectService.registerProject(registerProjectRequest, user);
    }

    @GetMapping(value = "", produces = "application/json")
    public List<ProjectInfoResponse> getProjectList(@AuthenticationPrincipal User user) {
        return projectService.getProjectListByUser(user);
    }

    @PatchMapping(value = "/{projectId}", produces = "application/json")
    public ProjectSettingInfoResponse updateProject(@PathVariable Long projectId,
                                                    @RequestBody @Valid FixProjectRequest fixProjectRequest,
                                                    @AuthenticationPrincipal User user) {
        return projectService.updateProject(projectId, fixProjectRequest, user);
    }

    @GetMapping("/visit-graph-infos")
    public VisitTotalGraphResponse getVisitGraphInfos(@AuthenticationPrincipal User user,
                                                @RequestParam
                                                @Min(value = 1, message = "프로젝트 아이디는 1 이상의 수로 입력해야 합니다.") Long projectId,
                                                @RequestParam @ChartPeriod String range,
                                                @RequestParam @ChartPeriod String interval){

        return projectService.getVisitGraphInfos(projectId, range, interval, user);
    }
    @GetMapping("/daily-visit-graph-infos")
    public DailyVisitInfoResponse getDailyVisitInfos(@AuthenticationPrincipal User user,
                                                     @RequestParam
                                                     @Min(value = 1, message = "프로젝트 아이디는 1 이상의 수로 입력해야 합니다.") Long projectId,
                                                     @RequestParam @ChartPeriod String range){
        return projectService.getDailyVisitInfos(projectId, range, user);
    }
    @GetMapping("/recent-visit-counts")
    public RecentVisitInfoResponse getRecentVisitCounts(@AuthenticationPrincipal User user,
                                                        @RequestParam @Min(value = 1, message = "프로젝트 아이디는 1 이상의 수로 입력해야 합니다.") Long projectId
    ){
        return projectService.getRecentVisitCounts(projectId, user);
    }
    private String getTokenOwner() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
