package com.developlife.reviewtwits.project;

import com.developlife.reviewtwits.entity.Product;
import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.StatInfo;
import com.developlife.reviewtwits.entity.User;
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
    public static final String projectName = "project-name";
    public static final String projectDescription = "프로젝트 설명";
    public static final String uriPattern = "/products";
    public static final String wrongUriPattern = "\\products";
    public static final ProjectCategory category = ProjectCategory.쇼핑;
    public static final Language language = Language.한국어;
    public static final String projectColor = "#FFFFFF";
    public static final ProjectPricePlan pricePlan = ProjectPricePlan.FREE_PLAN;

    public static final String productUrl = "http://www.example.com/123";
    public static final String exampleRange = "6mo";
    public static final String exampleInterval = "3d";
    public static final String wrongRange = "3s";
    public static final String notExistProjectName = "not_existed_project_name";
    public static final String wrongProjectName = "잘못된 이름";
    public static final String exampleEndDate = "2023-04-01";
    public static final int exampleCount = 3;

    public static RegisterProjectRequest 프로젝트생성요청_생성(int index) {
        return RegisterProjectRequest.builder()
                .projectName(projectName + "_" + index)
                .projectDescription(projectDescription)
                .uriPattern(uriPattern)
                .category(category.toString())
                .language(language.toString())
                .projectColor(projectColor)
                .build();
    }

    public static RegisterProjectRequest 프로젝트생성요청_잘못된이름_생성(int index){
        return RegisterProjectRequest.builder()
                .projectName(wrongProjectName + "_" + index)
                .projectDescription(projectDescription)
                .uriPattern(uriPattern)
                .category(category.toString())
                .language(language.toString())
                .projectColor(projectColor)
                .build();
    }

    public static RegisterProjectRequest 프로젝트생성요청_잘못된URI_생성(int index) {
        return RegisterProjectRequest.builder()
                .projectName(projectName + "_" + index)
                .projectDescription(projectDescription)
                .uriPattern(wrongUriPattern)
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
    public static StatInfo 통계정보_생성(Project project, Product product, User user, int year, int month, int day, int hour){
        return StatInfo.builder()
                .product(product)
                .user(project.getUser())
                .project(project)
                .user(user)
                .createdDate(LocalDateTime.of(year, month, day, hour, 0,0,0))
                .build();
    }
}
