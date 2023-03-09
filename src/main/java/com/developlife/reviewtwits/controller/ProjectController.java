package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.message.request.project.RegisterProjectRequest;
import com.developlife.reviewtwits.message.response.project.ProjectInfoResponse;
import com.developlife.reviewtwits.service.ProjectService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ghdic
 * @since 2023/03/10
 */
@RestController
@RequestMapping("/projects")
public class ProjectController {
    ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping(value = "", produces = "application/json")
    public void registerProject(@RequestBody RegisterProjectRequest registerProjectRequest) {
        String accountId = getTokenOwner();
        projectService.registerProject(registerProjectRequest, accountId);
    }

    @GetMapping(value = "", produces = "application/json")
    public List<ProjectInfoResponse> getProjectList() {
        String accountId = getTokenOwner();
        return projectService.getProjectListByUser(accountId);
    }

    private String getTokenOwner() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
