package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.project.ProjectIdNotFoundException;
import com.developlife.reviewtwits.exception.user.AccessResourceDeniedException;
import com.developlife.reviewtwits.exception.user.AccountIdNotFoundException;
import com.developlife.reviewtwits.mapper.ProjectMapper;
import com.developlife.reviewtwits.message.request.project.FixProjectRequest;
import com.developlife.reviewtwits.message.request.project.RegisterProjectRequest;
import com.developlife.reviewtwits.message.response.project.ProjectInfoResponse;
import com.developlife.reviewtwits.message.response.project.ProjectSettingInfoResponse;
import com.developlife.reviewtwits.repository.ProjectRepository;
import com.developlife.reviewtwits.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author ghdic
 * @since 2023/03/10
 */
@Service
public class ProjectService {

    ProjectRepository projectRepository;
    UserRepository userRepository;
    ProjectMapper projectMapper;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMapper = projectMapper;
    }

    @Transactional
    public void registerProject(RegisterProjectRequest registerProjectRequest, String accountId) {
        User user = userRepository.findByAccountId(accountId)
            .orElseThrow(() -> new AccountIdNotFoundException("해당 유저가 존재하지 않습니다."));

        Project project = projectMapper.toProject(registerProjectRequest);
        project.setUser(user);
        projectRepository.save(project);
    }

    public List<ProjectInfoResponse> getProjectListByUser(String accountId) {
        List<Project> projectList = projectRepository.findProjectsByUser_AccountId(accountId);
        return projectMapper.toProjectInfoResponseList(projectList);
    }

    @Transactional
    public ProjectSettingInfoResponse updateProject(Long projectId, FixProjectRequest fixProjectRequest, String accountId) {
        Project project = projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectIdNotFoundException("해당 프로젝트가 존재하지 않습니다."));
        if (!project.getUser().getAccountId().equals(accountId)) {
            throw new AccessResourceDeniedException("해당 리소스에 접근할 수 있는 권하이 없습니다.");
        }
        projectMapper.updateProjectFromFixProjectRequest(fixProjectRequest, project);
        projectRepository.save(project);
        return projectMapper.toProjectSettingInfoResponse(project);
    }

    public Long getProjectIdFromAccountId(String accountId) {
        Project project = projectRepository.findFirstByUser_AccountId(accountId)
            .orElseThrow(() -> new AccountIdNotFoundException("해당 유저가 존재하지 않습니다."));
        return project.getProjectId();
    }
}
