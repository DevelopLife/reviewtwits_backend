package com.developlife.reviewtwits.mapper;

import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.project.RegisterProjectRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "user", ignore = true)
    Project toProject(RegisterProjectRequest registerProjectRequest);
}
