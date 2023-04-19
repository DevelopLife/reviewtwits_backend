package com.developlife.reviewtwits.sns;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Snippet;

import static com.developlife.reviewtwits.DocumentFormatProvider.required;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.*;


/**
 * @author WhalesBob
 * @since 2023-03-20
 */
public class SnsDocument {
    public static final Snippet followRequestField = requestFields(
            fieldWithPath("targetUserAccountId").type(JsonFieldType.STRING)
                    .attributes(required()).description("팔로우하려는 계정의 accountId, 이메일 형식이어야 합니다.")
    );

    public static final Snippet followIdField = pathParameters(
            parameterWithName("accountId").attributes(required()).description("유저 계정 아이디")
    );

    public static final Snippet userNicknameField = pathParameters(
            parameterWithName("nickname").attributes(required()).description("유저 닉네임")
    );

    public static final Snippet snsFollowResponseField = responseFields(
            fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("유저 DB 아이디"),
            fieldWithPath("[].nickname").type(JsonFieldType.STRING).description("유저닉네임"),
            fieldWithPath("[].accountId").type(JsonFieldType.STRING).description("유저 계정"),
            fieldWithPath("[].introduceText").type(JsonFieldType.STRING).description("유저 한줄소개").optional(),
            fieldWithPath("[].detailIntroduce").type(JsonFieldType.STRING).description("유저 한줄소개").optional(),
            fieldWithPath("[].profileImageUrl").type(JsonFieldType.STRING).description("프로필이미지 파일이름").optional(),
            fieldWithPath("[].reviewCount").type(JsonFieldType.NUMBER).description("유저작성 리뷰 수").optional(),
            fieldWithPath("[].followers").type(JsonFieldType.NUMBER).description("팔로우 수").optional(),
            fieldWithPath("[].followings").type(JsonFieldType.NUMBER).description("팔로잉 수").optional()
    );

    public static final Snippet followResultResponseField = responseFields(
            fieldWithPath("followId").type(JsonFieldType.NUMBER).description("팔로우 아이디"),

            fieldWithPath("userInfoResponse.userId").type(JsonFieldType.NUMBER).description("유저 DB 아이디"),
            fieldWithPath("userInfoResponse.nickname").type(JsonFieldType.STRING).description("유저닉네임"),
            fieldWithPath("userInfoResponse.accountId").type(JsonFieldType.STRING).description("유저 계정"),
            fieldWithPath("userInfoResponse.introduceText").type(JsonFieldType.STRING).description("유저 한줄소개").optional(),
            fieldWithPath("userInfoResponse.detailIntroduce").type(JsonFieldType.STRING).description("유저 한줄소개").optional(),
            fieldWithPath("userInfoResponse.profileImageUrl").type(JsonFieldType.ARRAY).description("프로필이미지 파일이름").optional(),
            fieldWithPath("userInfoResponse.reviewCount").type(JsonFieldType.NUMBER).description("유저작성 리뷰 수").optional(),
            fieldWithPath("userInfoResponse.followers").type(JsonFieldType.NUMBER).description("팔로우 수").optional(),
            fieldWithPath("userInfoResponse.followings").type(JsonFieldType.NUMBER).description("팔로잉 수").optional(),

            fieldWithPath("targetUserInfoResponse.userId").type(JsonFieldType.NUMBER).description("유저 DB 아이디"),
            fieldWithPath("targetUserInfoResponse.nickname").type(JsonFieldType.STRING).description("유저닉네임"),
            fieldWithPath("targetUserInfoResponse.accountId").type(JsonFieldType.STRING).description("유저 계정"),
            fieldWithPath("targetUserInfoResponse.introduceText").type(JsonFieldType.STRING).description("유저 한줄소개").optional(),
            fieldWithPath("targetUserInfoResponse.detailIntroduce").type(JsonFieldType.STRING).description("유저 한줄소개").optional(),
            fieldWithPath("targetUserInfoResponse.profileImageUrl").type(JsonFieldType.ARRAY).description("프로필이미지 파일이름").optional(),
            fieldWithPath("targetUserInfoResponse.reviewCount").type(JsonFieldType.NUMBER).description("유저작성 리뷰 수").optional(),
            fieldWithPath("targetUserInfoResponse.followers").type(JsonFieldType.NUMBER).description("팔로우 수").optional(),
            fieldWithPath("targetUserInfoResponse.followings").type(JsonFieldType.NUMBER).description("팔로잉 수").optional(),

            fieldWithPath("followBackFlag").type(JsonFieldType.BOOLEAN).description("팔로우백 여부")
    );

    public static final Snippet SearchAllSnsRequest = requestParameters(
        parameterWithName("searchKey").attributes(required()).description("검색어(2-20글자)")
    );

    public static final Snippet SearchAllSnsResponse = responseFields(
        fieldWithPath("itemList[].itemId").type(JsonFieldType.NUMBER).description("아이템아이디"),
        fieldWithPath("itemList[].productName").type(JsonFieldType.STRING).description("상품이름"),
        fieldWithPath("itemList[].productImageUrl").type(JsonFieldType.STRING).description("상품이미지URL"),
        fieldWithPath("itemList[].score").type(JsonFieldType.NUMBER).description("평균평점"),
        fieldWithPath("itemList[].url").type(JsonFieldType.STRING).description("상품정보페이지URL"),
        fieldWithPath("reviewList[].reviewId").type(JsonFieldType.NUMBER).description("리뷰아이디"),
        fieldWithPath("reviewList[].createdDate").type(JsonFieldType.ARRAY).description("생성일"),
        fieldWithPath("reviewList[].lastModifiedDate").type(JsonFieldType.ARRAY).description("최근수정일"),
        fieldWithPath("reviewList[].userInfo.nickname").type(JsonFieldType.STRING).description("닉네임"),
        fieldWithPath("reviewList[].userInfo.userId").type(JsonFieldType.NUMBER).description("유저 DB 아이디"),
        fieldWithPath("reviewList[].userInfo.accountId").type(JsonFieldType.STRING).description("아이디"),
        fieldWithPath("reviewList[].userInfo.introduceText").type(JsonFieldType.STRING).description("자기소개"),
        fieldWithPath("reviewList[].userInfo.detailIntroduce").type(JsonFieldType.STRING).description("유저 한줄소개").optional(),
        fieldWithPath("reviewList[].userInfo.profileImageUrl").type(JsonFieldType.STRING).description("프로필이미지URL").optional(),
        fieldWithPath("reviewList[].userInfo.reviewCount").type(JsonFieldType.NUMBER).description("유저작성 리뷰 수").optional(),
        fieldWithPath("reviewList[].userInfo.followers").type(JsonFieldType.NUMBER).description("팔로우 수").optional(),
        fieldWithPath("reviewList[].userInfo.followings").type(JsonFieldType.NUMBER).description("팔로잉 수").optional(),
        fieldWithPath("reviewList[].content").type(JsonFieldType.STRING).description("리뷰콘텐츠").optional(),
        fieldWithPath("reviewList[].productUrl").type(JsonFieldType.STRING).description("리뷰상품구매URL"),
        fieldWithPath("reviewList[].productName").type(JsonFieldType.STRING).description("리뷰상품이름"),
        fieldWithPath("reviewList[].score").type(JsonFieldType.NUMBER).description("평점"),
        fieldWithPath("reviewList[].reviewImageUrlList[]").type(JsonFieldType.ARRAY).description("리뷰이미지리스트"),
        fieldWithPath("reviewList[].commentCount").type(JsonFieldType.NUMBER).description("댓글수"),
        fieldWithPath("reviewList[].reactionResponses.*.isReacted").type(JsonFieldType.BOOLEAN).description("테스트용1"),
        fieldWithPath("reviewList[].reactionResponses.*.count").type(JsonFieldType.NUMBER).description("테스트용2"),
        fieldWithPath("reviewList[].isScrapped").type(JsonFieldType.BOOLEAN).description("스크랩여부")
    );
    public static final Snippet RecommendProductResponse = responseFields(
        fieldWithPath("[].itemId").type(JsonFieldType.NUMBER).description("아이템아이디"),
        fieldWithPath("[].productName").type(JsonFieldType.STRING).description("상품이름"),
        fieldWithPath("[].productImageUrl").type(JsonFieldType.STRING).description("상품이미지URL"),
        fieldWithPath("[].score").type(JsonFieldType.NUMBER).description("평균평점"),
        fieldWithPath("[].url").type(JsonFieldType.STRING).description("상품정보페이지URL")
    );
    public static final Snippet FollowerRecommendResponse = responseFields(
        fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("유저 아이디"),
        fieldWithPath("[].nickname").type(JsonFieldType.STRING).description("유저닉네임"),
        fieldWithPath("[].accountId").type(JsonFieldType.STRING).description("유저 계정"),
        fieldWithPath("[].introduceText").type(JsonFieldType.STRING).description("유저 한줄소개").optional(),
        fieldWithPath("[].profileImageUrl").type(JsonFieldType.STRING).description("프로필이미지 파일이름").optional(),
        fieldWithPath("[].detailIntroduce").type(JsonFieldType.STRING).description("유저 상세소개").optional(),
        fieldWithPath("[].reviewCount").type(JsonFieldType.NUMBER).description("유저작성 리뷰 수"),
        fieldWithPath("[].followers").type(JsonFieldType.NUMBER).description("팔로우 수"),
        fieldWithPath("[].followings").type(JsonFieldType.NUMBER).description("팔로잉 수")
    );
    public static final Snippet UserProfileInfoResponse = responseFields(
        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 닉네임"),
        fieldWithPath("accountId").type(JsonFieldType.STRING).description("유저 계정"),
        fieldWithPath("nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
        fieldWithPath("introduceText").type(JsonFieldType.STRING).description("유저 한줄소개"),
        fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("유저 프로필이미지"),
        fieldWithPath("detailIntroduce").type(JsonFieldType.STRING).description("유저 상세소개").optional(),
        fieldWithPath("reviewCount").type(JsonFieldType.NUMBER).description("유저작성 리뷰 수"),
        fieldWithPath("followers").type(JsonFieldType.NUMBER).description("팔로우 수"),
        fieldWithPath("followings").type(JsonFieldType.NUMBER).description("팔로잉 수")
    );

    public static final Snippet UserSnsReviewResponse = responseFields(
            fieldWithPath("[].reviewId").type(JsonFieldType.NUMBER).description("리뷰아이디"),
            fieldWithPath("[].userInfo.nickname").type(JsonFieldType.STRING).description("닉네임"),
            fieldWithPath("[].userInfo.userId").type(JsonFieldType.NUMBER).description("유저 DB 아이디"),
            fieldWithPath("[].userInfo.accountId").type(JsonFieldType.STRING).description("아이디"),
            fieldWithPath("[].userInfo.introduceText").type(JsonFieldType.STRING).description("자기소개"),
            fieldWithPath("[].userInfo.detailIntroduce").type(JsonFieldType.STRING).description("유저 한줄소개").optional(),
            fieldWithPath("[].userInfo.profileImageUrl").type(JsonFieldType.STRING).description("프로필이미지URL").optional(),
            fieldWithPath("[].userInfo.reviewCount").type(JsonFieldType.NUMBER).description("유저작성 리뷰 수").optional(),
            fieldWithPath("[].userInfo.followers").type(JsonFieldType.NUMBER).description("팔로우 수").optional(),
            fieldWithPath("[].userInfo.followings").type(JsonFieldType.NUMBER).description("팔로잉 수").optional(),
            fieldWithPath("[].reviewImageUrlList[]").type(JsonFieldType.ARRAY).description("리뷰사진 리스트"),
            fieldWithPath("[].commentCount").type(JsonFieldType.NUMBER).description("댓글개수"),
            fieldWithPath("[].reactionCount").type(JsonFieldType.NUMBER).description("리액션개수")
    );
    public static final Snippet ReviewIdAndSizeField = requestParameters(
            parameterWithName("reviewId").description("리뷰의 아이디").optional(),
            parameterWithName("size").attributes(required()).description("요청하는 페이지 사이즈")
    );
}