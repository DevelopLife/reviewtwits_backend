package com.developlife.reviewtwits.project;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.snippet.Snippet;

import static com.developlife.reviewtwits.DocumentFormatProvider.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

/**
 * @author ghdic
 * @since 2023/03/09
 */
public class ProjectDocument {
    public static final Snippet RegisterProjectRequestField = requestFields(
        fieldWithPath("projectName").type(JsonFieldType.STRING).attributes(required()).description("프로젝트 이름 최대(최소2글자 최대 50글자)"),
        fieldWithPath("projectDescription").type(JsonFieldType.STRING).attributes(required()).description("프로젝트 설명(최대 200글자)"),
        fieldWithPath("uriPattern").type(JsonFieldType.STRING).attributes(required()).description("서비스 로드 되게할 URI패턴 “,”으로 구분 되게 여러개 적을 수 있음"),
        fieldWithPath("category").type(JsonFieldType.STRING).attributes(required()).description("카테고리(쇼핑, 영화, 게임)"),
        fieldWithPath("language").type(JsonFieldType.STRING).attributes(required()).description("서비스 언어(한국어, ENGLISH)"),
        fieldWithPath("projectColor").type(JsonFieldType.STRING).attributes(required()).description("프로젝트 색깔 hex코드"),
        fieldWithPath("pricePlan").type(JsonFieldType.STRING).description("가격 플랜").optional()
    );
    public static final Snippet ProjectInfoListResponseField = responseFields(
        fieldWithPath("[].projectId").type(JsonFieldType.STRING).description("프로젝트 아이디"),
        fieldWithPath("[].projectName").type(JsonFieldType.STRING).description("프로젝트 이름 최대(최소2글자 최대 50글자)"),
        fieldWithPath("[].projectDescription").type(JsonFieldType.STRING).description("프로젝트 설명(최대 200글자)"),
        fieldWithPath("[].projectColor").type(JsonFieldType.STRING).description("프로젝트 색깔 hex코드"),
        fieldWithPath("[].reviewCount").type(JsonFieldType.STRING).description("리뷰 수"),
        fieldWithPath("[].category").type(JsonFieldType.STRING).description("카테고리")
    );
    public static final Snippet FixProjectRequestField = requestFields(
        fieldWithPath("projectName").type(JsonFieldType.STRING).description("프로젝트 이름 최대(최소2글자 최대 50글자)").optional(),
        fieldWithPath("projectDescription").type(JsonFieldType.STRING).description("프로젝트 설명(최대 200글자)").optional(),
        fieldWithPath("uriPattern").type(JsonFieldType.STRING).description("서비스 로드 되게할 URI패턴 “,”으로 구분 되게 여러개 적을 수 있음").optional(),
        fieldWithPath("category").type(JsonFieldType.STRING).description("카테고리(쇼핑, 영화, 게임)").optional(),
        fieldWithPath("language").type(JsonFieldType.STRING).description("서비스 언어(한국어, ENGLISH)").optional(),
        fieldWithPath("projectColor").type(JsonFieldType.STRING).description("프로젝트 색깔 hex코드").optional(),
        fieldWithPath("pricePlan").type(JsonFieldType.STRING).description("가격 플랜").optional()
    );
    public static final Snippet ProjectSettingInfoResponseField = responseFields(
        fieldWithPath("projectId").type(JsonFieldType.STRING).description("프로젝트 아이디"),
        fieldWithPath("projectName").type(JsonFieldType.STRING).description("프로젝트 이름 최대(최소2글자 최대 50글자)"),
        fieldWithPath("projectDescription").type(JsonFieldType.STRING).description("프로젝트 설명(최대 200글자)"),
        fieldWithPath("uriPattern").type(JsonFieldType.STRING).description("서비스 로드 되게할 URI패턴 “,”으로 구분 되게 여러개 적을 수 있음"),
        fieldWithPath("category").type(JsonFieldType.STRING).description("카테고리(쇼핑, 영화, 게임)"),
        fieldWithPath("language").type(JsonFieldType.STRING).description("서비스 언어(한국어, ENGLISH)"),
        fieldWithPath("projectColor").type(JsonFieldType.STRING).description("프로젝트 색깔 hex코드"),
        fieldWithPath("pricePlan").type(JsonFieldType.STRING).description("가격 플랜")
    );
    public static final Snippet ProjectIdPathParam = pathParameters(
        RequestDocumentation.parameterWithName("projectId").description("프로젝트아이디")
    );
    public static final Snippet DailyVisitStatRequestParam = requestParameters(
            RequestDocumentation.parameterWithName("projectId").attributes(required()).description("프로젝트 아이디"),
            RequestDocumentation.parameterWithName("range").attributes(required()).description("요청하는 통계 범위 구간")
    );

    public static final Snippet ProjectInfoResponseField = responseFields(
        fieldWithPath("projectId").type(JsonFieldType.STRING).description("프로젝트 아이디"),
        fieldWithPath("projectName").type(JsonFieldType.STRING).description("프로젝트 이름 최대(최소2글자 최대 50글자)"),
        fieldWithPath("projectDescription").type(JsonFieldType.STRING).description("프로젝트 설명(최대 200글자)"),
        fieldWithPath("projectColor").type(JsonFieldType.STRING).description("프로젝트 색깔 hex코드"),
        fieldWithPath("reviewCount").type(JsonFieldType.STRING).description("리뷰 수"),
        fieldWithPath("category").type(JsonFieldType.STRING).description("카테고리")
    );

    public static final Snippet DailyVisitInfoResponseFields = responseFields(
            fieldWithPath("range").type(JsonFieldType.STRING).description("요청한 통계 범위"),
            fieldWithPath("visitInfo.timeStamp").type(JsonFieldType.ARRAY).description("방문 날짜"),
            fieldWithPath("visitInfo.visitCount").type(JsonFieldType.ARRAY).description("방문 수"),
            fieldWithPath("visitInfo.previousCompare").type(JsonFieldType.ARRAY).description("어제 방문과의 비교")
    );
}
