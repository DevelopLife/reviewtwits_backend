package com.developlife.reviewtwits.mapper;

import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.project.FixProjectRequest;
import com.developlife.reviewtwits.message.request.project.RegisterProjectRequest;
import com.developlife.reviewtwits.message.response.email.FindIdsEmailResponse;
import com.developlife.reviewtwits.message.response.project.ProjectInfoResponse;
import com.developlife.reviewtwits.message.response.project.ProjectSettingInfoResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ProjectMapper {

    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    Project toProject(RegisterProjectRequest registerProjectRequest);

    ProjectInfoResponse toProjectInfoResponse(Project project);
    List<ProjectInfoResponse> toProjectInfoResponseList(List<Project> projects);

    ProjectSettingInfoResponse toProjectSettingInfoResponse(Project project);
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    void updateProjectFromFixProjectRequest(FixProjectRequest fixProjectRequest, @MappingTarget Project project);
}
