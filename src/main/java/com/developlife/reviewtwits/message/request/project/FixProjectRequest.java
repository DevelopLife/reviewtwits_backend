package com.developlife.reviewtwits.message.request.project;

import com.developlife.reviewtwits.type.project.Language;
import com.developlife.reviewtwits.type.project.ProjectCategory;
import com.developlife.reviewtwits.type.project.ProjectPricePlan;
import lombok.Builder;

/**
 * @author ghdic
 * @since 2023/03/10
 */
public record FixProjectRequest(String projectName, String projectDescription, String uriPattern, ProjectCategory category, Language language, String projectColor, ProjectPricePlan pricePlan) {
    @Builder
    public FixProjectRequest {
    }
}
