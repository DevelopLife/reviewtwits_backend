package com.developlife.reviewtwits.project;

import com.developlife.reviewtwits.entity.Product;
import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.StatInfo;
import com.developlife.reviewtwits.message.request.project.FixProjectRequest;
import com.developlife.reviewtwits.message.request.project.RegisterProjectRequest;
import com.developlife.reviewtwits.type.project.Language;
import com.developlife.reviewtwits.type.project.ProjectCategory;
import com.developlife.reviewtwits.type.project.ProjectPricePlan;

import java.time.LocalDateTime;

/**
 * @author ghdic
 * @since 2023/03/09
 */
public class ProjectSteps {
    public static final String projectName = "프로젝트 이름";
    public static final String projectDescription = "프로젝트 설명";
    public static final String uriPattern = "/products";
    public static final ProjectCategory category = ProjectCategory.쇼핑;
    public static final Language language = Language.한국어;
    public static final String projectColor = "#FFFFFF";
    public static final ProjectPricePlan pricePlan = ProjectPricePlan.FREE_PLAN;

    public static final String productUrl = "http://www.example.com/123";
    public static final String exampleRange = "3mo";
    public static final String wrongRange = "3s";
    public static final long wrongProjectId = -1L;

    public static RegisterProjectRequest 프로젝트생성요청_생성() {
        return RegisterProjectRequest.builder()
                .projectName(projectName)
                .projectDescription(projectDescription)
                .uriPattern(uriPattern)
                .category(category.toString())
                .language(language.toString())
                .projectColor(projectColor)
                .build();
    }

    public static FixProjectRequest 프로젝트수정요청_생성() {
        return FixProjectRequest.builder()
            .projectName("인생마린 프로젝트")
            .language(Language.ENGLISH.toString())
            .build();
    }

    public static Product 임시_제품정보_생성(Project project){
        return Product.builder()
                .project(project)
                .productUrl(productUrl)
                .build();
    }
    public static StatInfo 통계정보_생성(Project project, int year, int month, int day, int hour){
        return StatInfo.builder()
                .project(project)
                .createdDate(LocalDateTime.of(year, month, day, hour, 0))
                .build();
    }
}
