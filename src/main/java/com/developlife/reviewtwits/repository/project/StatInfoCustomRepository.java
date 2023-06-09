package com.developlife.reviewtwits.repository.project;

import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.message.response.statistics.SimpleProjectInfoResponse;

public interface StatInfoCustomRepository {
    SimpleProjectInfoResponse findSimpleProjectInfo(Project project);
}
