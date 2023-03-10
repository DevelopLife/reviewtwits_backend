package com.developlife.reviewtwits.message.response.project;

import com.developlife.reviewtwits.type.project.Language;
import com.developlife.reviewtwits.type.project.ProjectCategory;
import com.developlife.reviewtwits.type.project.ProjectPricePlan;

/**
 * @author ghdic
 * @since 2023/03/10
 */
public record ProjectSettingInfoResponse(String projectId, String projectName, String projectDescription, String uriPattern, ProjectCategory category, Language language, String projectColor, ProjectPricePlan pricePlan) {
}
