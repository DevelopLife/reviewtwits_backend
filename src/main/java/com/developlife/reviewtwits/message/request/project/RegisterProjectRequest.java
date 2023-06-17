package com.developlife.reviewtwits.message.request.project;

import com.developlife.reviewtwits.message.annotation.project.*;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record RegisterProjectRequest(
    @NotBlank(message = "프로젝트 이름을 입력해주세요")
    @Size(min = 2, max = 30, message = "프로젝트 이름은 2자 이상 30자 이하로 입력해주세요")
    @ProjectName
    String projectName,
    @NotBlank(message = "프로젝트 설명을 입력해주세요")
    @Size(max = 100, message = "프로젝트 설명은 100자 이하로 입력해주세요")
    String projectDescription,
    @NotBlank(message = "URI를 입력해주세요")
    @URI
    String uriPattern,
    @NotBlank(message = "프로젝트 카테고리를 선택해주세요")
    @ProjectCategory
    String category,
    @NotBlank(message = "프로젝트 언어를 선택해주세요")
    @Language
    String language,
    @NotBlank(message = "프로젝트 색상을 선택해주세요")
    @Color
    String projectColor,
    @ProjectPricePlan
    String pricePlan) {
    @Builder
    public RegisterProjectRequest {
    }
}
