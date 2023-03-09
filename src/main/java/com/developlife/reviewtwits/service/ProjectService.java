package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.user.AccountIdNotFoundException;
import com.developlife.reviewtwits.mapper.ProjectMapper;
import com.developlife.reviewtwits.message.request.project.RegisterProjectRequest;
import com.developlife.reviewtwits.message.response.project.ProjectInfoResponse;
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
}
