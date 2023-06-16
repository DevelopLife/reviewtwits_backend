package com.developlife.reviewtwits.statistics;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.snippet.Snippet;

import static com.developlife.reviewtwits.DocumentFormatProvider.required;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

/**
 * @author WhalesBob
 * @since 2023-04-23
 */
public class StatDocument {
    public static final Snippet AccessTokenHeader = requestHeaders(
            headerWithName("X-AUTH-TOKEN").description("access token").optional()
    );
    public static final Snippet statMessageRequestField = requestFields(
            fieldWithPath("inflowUrl").type(JsonFieldType.VARIES).attributes(required()).description("검색 유입 URL"),
            fieldWithPath("productUrl").type(JsonFieldType.STRING).attributes(required()).description("상품 URL"),
            fieldWithPath("device").type(JsonFieldType.STRING).attributes(required()).description("디바이스 정보")
    );

    public static final Snippet DailyVisitStatRequestParam = requestParameters(
            RequestDocumentation.parameterWithName("projectId").attributes(required()).description("프로젝트 아이디"),
            RequestDocumentation.parameterWithName("range").attributes(required()).description("요청하는 통계 범위 구간")
    );
    public static final Snippet VisitGraphInfoRequestParamFields = requestParameters(
            RequestDocumentation.parameterWithName("projectId").attributes(required()).description("프로젝트 아이디"),
            RequestDocumentation.parameterWithName("range").attributes(required()).description("요청하는 통계 범위 구간"),
            RequestDocumentation.parameterWithName("interval").attributes(required()).description("요청하는 통계 기준 구간"),
            RequestDocumentation.parameterWithName("endDate").description("요청 통계의 마지막 날짜").optional()
    );

    public static final Snippet projectIdRequestParamField = requestParameters(
            RequestDocumentation.parameterWithName("projectId").attributes(required()).description("프로젝트 아이디")
    );

    public static final Snippet savedStatResponseField = responseFields(
            fieldWithPath("statId").type(JsonFieldType.NUMBER).description("통계정보 아이디"),
            fieldWithPath("createdDate").type(JsonFieldType.STRING).description("통계 생성 날짜"),
            fieldWithPath("inflowUrl").type(JsonFieldType.VARIES).description("검색 유입 URL"),
            fieldWithPath("productUrl").type(JsonFieldType.STRING).description("상품 URL"),
            fieldWithPath("userInfo").type(JsonFieldType.VARIES).description("유저 정보"),
            fieldWithPath("userInfo.userId").type(JsonFieldType.NUMBER).description("유저 아이디").optional(),
            fieldWithPath("userInfo.nickname").type(JsonFieldType.STRING).description("유저 닉네임").optional(),
            fieldWithPath("userInfo.accountId").type(JsonFieldType.STRING).description("유저 계정 아이디").optional(),
            fieldWithPath("userInfo.introduceText").type(JsonFieldType.STRING).description("유저 한줄소개").optional(),
            fieldWithPath("userInfo.profileImageUrl").type(JsonFieldType.STRING).description("유저 프로필 이미지").optional(),
            fieldWithPath("userInfo.detailIntroduce").type(JsonFieldType.STRING).description("유저 세부소개").optional(),
            fieldWithPath("userInfo.reviewCount").type(JsonFieldType.NUMBER).description("리뷰 숫자").optional(),
            fieldWithPath("userInfo.followers").type(JsonFieldType.NUMBER).description("팔로워 숫자").optional(),
            fieldWithPath("userInfo.followings").type(JsonFieldType.NUMBER).description("팔로잉 숫자").optional(),
            fieldWithPath("userInfo.isFollowed").type(JsonFieldType.BOOLEAN).description("요청한 유저가 팔로우했는지 여부").optional(),
            fieldWithPath("projectId").type(JsonFieldType.NUMBER).description("프로젝트 아이디"),
            fieldWithPath("productId").type(JsonFieldType.NUMBER).description("상품 아이디"),
            fieldWithPath("deviceInfo").type(JsonFieldType.STRING).description("디바이스 정보")
    );

    public static final Snippet DailyVisitInfoResponseFields = responseFields(
            fieldWithPath("range").type(JsonFieldType.STRING).description("요청한 통계 범위"),
            fieldWithPath("visitInfo").type(JsonFieldType.ARRAY).description("방문 정보"),
            fieldWithPath("visitInfo[].timeStamp").type(JsonFieldType.STRING).description("방문 날짜"),
            fieldWithPath("visitInfo[].visitCount").type(JsonFieldType.NUMBER).description("방문 수"),
            fieldWithPath("visitInfo[].previousCompare").type(JsonFieldType.NUMBER).description("어제 방문과의 비교")
    );
    public static final Snippet RecentVisitStatResponseFields = responseFields(
            fieldWithPath("todayVisit").type(JsonFieldType.NUMBER).description("오늘 방문 수"),
            fieldWithPath("yesterdayVisit").type(JsonFieldType.NUMBER).description("어제 방문 수"),
            fieldWithPath("totalVisit").type(JsonFieldType.NUMBER).description("총 방문 수")
    );
    public static final Snippet VisitGraphStatResponseFields = responseFields(
            fieldWithPath("range").type(JsonFieldType.STRING).description("요청한 통계 범위"),
            fieldWithPath("interval").type(JsonFieldType.STRING).description("요청한 통계 기준"),
            fieldWithPath("todayVisit").type(JsonFieldType.NUMBER).description("오늘 방문 수"),
            fieldWithPath("yesterdayVisit").type(JsonFieldType.NUMBER).description("어제 방문 수"),
            fieldWithPath("totalVisit").type(JsonFieldType.NUMBER).description("총 방문 수"),
            fieldWithPath("visitInfo").type(JsonFieldType.ARRAY).description("방문 정보"),
            fieldWithPath("visitInfo[].timeStamp").type(JsonFieldType.STRING).description("방문 날짜"),
            fieldWithPath("visitInfo[].visitCount").type(JsonFieldType.NUMBER).description("방문 수"),
            fieldWithPath("visitInfo[].previousCompare").type(JsonFieldType.NUMBER).description("어제 방문과의 비교")
    );
    public static final Snippet productStatisticsResponseFields = responseFields(
            fieldWithPath("[].productName").type(JsonFieldType.VARIES).description("상품 이름"),
            fieldWithPath("[].visitCount").type(JsonFieldType.NUMBER).description("상품 방문 수"),
            fieldWithPath("[].reviewCount").type(JsonFieldType.NUMBER).description("상품 리뷰 수"),
            fieldWithPath("[].mainAge").type(JsonFieldType.NUMBER).description("방문 유저 주요 연령대"),
            fieldWithPath("[].mainGender").type(JsonFieldType.STRING).description("방문 유저 주요 성별"),
            fieldWithPath("[].averageScore").type(JsonFieldType.NUMBER).description("상품 평균 리뷰 점수")
    );
    public static final Snippet requestInflowInfosResponseFields = responseFields(
            fieldWithPath("total").type(JsonFieldType.NUMBER).description("검색 유입 총 수"),
            fieldWithPath("naver").type(JsonFieldType.NUMBER).description("네이버 검색 유입 수"),
            fieldWithPath("google").type(JsonFieldType.NUMBER).description("구글 검색 유입 수"),
            fieldWithPath("daum").type(JsonFieldType.NUMBER).description("다음 검색 유입 수"),
            fieldWithPath("zoom").type(JsonFieldType.NUMBER).description("줌 검색 유입 수"),
            fieldWithPath("bing").type(JsonFieldType.NUMBER).description("빙 검색 유입 수"),
            fieldWithPath("yahoo").type(JsonFieldType.NUMBER).description("야후 검색 유입 수"),
            fieldWithPath("etc").type(JsonFieldType.NUMBER).description("기타 검색 유입 수")
    );
    public static final Snippet simpleProjectInfoResponseFields = responseFields(
            fieldWithPath("monthlyVisitCount").type(JsonFieldType.NUMBER).description("월간 방문 수"),
            fieldWithPath("dailyReviewCount").type(JsonFieldType.NUMBER).description("일간 리뷰 수"),
            fieldWithPath("pendingReviewCount").type(JsonFieldType.NUMBER).description("대기중인 리뷰 수"),
            fieldWithPath("registeredProductCount").type(JsonFieldType.NUMBER).description("등록된 상품 수")
    );
}