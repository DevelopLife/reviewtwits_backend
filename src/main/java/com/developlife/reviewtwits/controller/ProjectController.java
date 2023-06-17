package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.project.ProjectNameDuplicateException;
import com.developlife.reviewtwits.message.request.project.FixProjectRequest;
import com.developlife.reviewtwits.message.request.project.RegisterProjectRequest;
import com.developlife.reviewtwits.message.response.project.*;
import com.developlife.reviewtwits.service.ProjectService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public ProjectInfoResponse registerProject(@RequestBody @Valid RegisterProjectRequest registerProjectRequest,
                                @AuthenticationPrincipal User user) {
        try{
            return projectService.registerProject(registerProjectRequest, user);
        }catch(DataIntegrityViolationException e){
            throw new ProjectNameDuplicateException("이미 존재하는 프로젝트 이름입니다.");
        }
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
}
