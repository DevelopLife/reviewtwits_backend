package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.project.ProjectIdNotFoundException;
import com.developlife.reviewtwits.exception.user.AccessDeniedException;
import com.developlife.reviewtwits.exception.user.AccessResourceDeniedException;
import com.developlife.reviewtwits.exception.user.AccountIdNotFoundException;
import com.developlife.reviewtwits.mapper.ProjectMapper;
import com.developlife.reviewtwits.message.request.project.FixProjectRequest;
import com.developlife.reviewtwits.message.request.project.RegisterProjectRequest;
import com.developlife.reviewtwits.message.response.project.*;
import com.developlife.reviewtwits.repository.project.ProjectRepository;
import com.developlife.reviewtwits.repository.project.StatInfoRepository;
import com.developlife.reviewtwits.repository.UserRepository;
import com.developlife.reviewtwits.type.project.ChartPeriodUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author ghdic
 * @since 2023/03/10
 */
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final StatInfoRepository statInfoRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public ProjectInfoResponse registerProject(RegisterProjectRequest registerProjectRequest, User user) {
        Project project = projectMapper.toProject(registerProjectRequest);
        project.setUser(user);
        projectRepository.save(project);

        return projectMapper.toProjectInfoResponse(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectInfoResponse> getProjectListByUser(User user) {
        List<Project> projectList = projectRepository.findProjectsByUser_AccountId(user.getAccountId());
        return projectMapper.toProjectInfoResponseList(projectList);
    }

    @Transactional
    public ProjectSettingInfoResponse updateProject(Long projectId, FixProjectRequest fixProjectRequest, User user) {
        Project project = projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectIdNotFoundException("해당 프로젝트가 존재하지 않습니다."));
        if (!project.getUser().getAccountId().equals(user.getAccountId())) {
            throw new AccessResourceDeniedException("해당 리소스에 접근할 수 있는 권하이 없습니다.");
        }
        projectMapper.updateProjectFromFixProjectRequest(fixProjectRequest, project);
        projectRepository.save(project);
        return projectMapper.toProjectSettingInfoResponse(project);
    }

    @Transactional(readOnly = true)
    public Long getProjectIdFromAccountId(String accountId) {
        Project project = projectRepository.findFirstByUser_AccountId(accountId)
            .orElseThrow(() -> new AccountIdNotFoundException("해당 유저가 존재하지 않습니다."));
        return project.getProjectId();
    }

    public DailyVisitInfoResponse getDailyVisitInfos(Long projectId, String inputRange, User user) {

        Project project = getProject(projectId, user);
        ChartPeriodUnit range = ChartPeriodUnit.findByInputValue(inputRange);

        VisitInfoResponse visitInfo = statInfoRepository.findByPeriod(project, range, ChartPeriodUnit.ONE_DAY);

        return DailyVisitInfoResponse.builder()
                .range(inputRange)
                .visitInfo(visitInfo)
                .build();
    }
    private Project getProject(Long projectId, User user) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectIdNotFoundException("해당 프로젝트가 존재하지 않습니다."));

        if (!project.getUser().getAccountId().equals(user.getAccountId())) {
            throw new AccessDeniedException("해당 프로젝트 통계정보에 접근할 수 있는 권한이 없습니다.");
        }
        return project;
    }
}
