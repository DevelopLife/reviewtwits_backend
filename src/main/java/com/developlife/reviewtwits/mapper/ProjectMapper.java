package com.developlife.reviewtwits.mapper;

import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.project.RegisterProjectRequest;
import com.developlife.reviewtwits.message.response.email.FindIdsEmailResponse;
import com.developlife.reviewtwits.message.response.project.ProjectInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "user", ignore = true)
    Project toProject(RegisterProjectRequest registerProjectRequest);

    ProjectInfoResponse toProjectInfoResponse(Project project);

    List<ProjectInfoResponse> toProjectInfoResponseList(List<Project> projects);
}
