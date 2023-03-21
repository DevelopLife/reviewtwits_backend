package com.developlife.reviewtwits.message.request.project;

import com.developlife.reviewtwits.message.annotation.project.Color;
import com.developlife.reviewtwits.message.annotation.project.Language;
import com.developlife.reviewtwits.message.annotation.project.ProjectCategory;
import com.developlife.reviewtwits.message.annotation.project.URI;
import lombok.Builder;

import javax.validation.constraints.Size;

/**
 * @author ghdic
 * @since 2023/03/10
 */
public record FixProjectRequest(
    @Size(min = 2, max = 30, message = "프로젝트 이름은 2자 이상 30자 이하로 입력해주세요")
    String projectName,
    @Size(max = 100, message = "프로젝트 설명은 100자 이하로 입력해주세요")
    String projectDescription,
    @URI
    String uriPattern,
    @ProjectCategory
    String category,
    @Language
    String language,
    @Color
    String projectColor,
    @ProjectCategory
    String pricePlan) {
    @Builder
    public FixProjectRequest {
    }
}
