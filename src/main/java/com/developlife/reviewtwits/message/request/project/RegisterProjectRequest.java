package com.developlife.reviewtwits.message.request.project;

import com.developlife.reviewtwits.message.annotation.project.Color;
import com.developlife.reviewtwits.message.annotation.project.URI;
import com.developlife.reviewtwits.type.project.Language;
import com.developlife.reviewtwits.type.project.ProjectCategory;
import com.developlife.reviewtwits.type.project.ProjectPricePlan;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record RegisterProjectRequest(
    @NotBlank(message = "프로젝트 이름을 입력해주세요")
    @Size(min = 2, max = 30, message = "프로젝트 이름은 2자 이상 30자 이하로 입력해주세요")
    String projectName,
    @NotBlank(message = "프로젝트 설명을 입력해주세요")
    @Size(max = 100, message = "프로젝트 설명은 100자 이하로 입력해주세요")
    String projectDescription,
    @NotBlank(message = "URI를 입력해주세요")
    @URI
    String uriPattern,
    @NotBlank(message = "프로젝트 카테고리를 선택해주세요")
    @com.developlife.reviewtwits.message.annotation.project.ProjectCategory
    String category,
    @NotBlank(message = "프로젝트 언어를 선택해주세요")
    @com.developlife.reviewtwits.message.annotation.project.Language
    String language,
    @NotBlank(message = "프로젝트 색상을 선택해주세요")
    @Color
    String projectColor,
    @com.developlife.reviewtwits.message.annotation.project.ProjectPricePlan
    String pricePlan) {
    @Builder
    public RegisterProjectRequest {
    }
}
