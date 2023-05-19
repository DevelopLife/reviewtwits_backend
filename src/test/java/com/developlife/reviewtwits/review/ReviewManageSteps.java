package com.developlife.reviewtwits.review;

import com.developlife.reviewtwits.message.request.review.ReviewApproveRequest;

/**
 * @author WhalesBob
 * @since 2023-05-19
 */
public class ReviewManageSteps {

    public static final String approve = "APPROVED";
    public static final String spam = "SPAM";
    public static final String wrongApprove = "WRONG";
    public static final Long wrongReviewId = 999999999999999999L;

    public static ReviewApproveRequest 리뷰_허가요청_생성(Long reviewId) {
        return ReviewApproveRequest.builder()
                .reviewId(reviewId)
                .approveType(approve)
                .build();
    }
    public static ReviewApproveRequest 리뷰_허가_스팸요청_생성(Long reviewId) {
        return ReviewApproveRequest.builder()
                .reviewId(reviewId)
                .approveType(spam)
                .build();
    }

    public static ReviewApproveRequest 리뷰_허가요청_잘못된아이디_생성() {
        return ReviewApproveRequest.builder()
                .reviewId(wrongReviewId)
                .approveType(approve)
                .build();
    }

    public static ReviewApproveRequest 리뷰_허가요청_잘못된요청_생성(Long reviewId){
        return ReviewApproveRequest.builder()
                .reviewId(reviewId)
                .approveType(wrongApprove)
                .build();
    }
    public static ReviewApproveRequest 리뷰_허가요청_리뷰아이디_누락(){
        return ReviewApproveRequest.builder()
                .approveType(approve)
                .build();
    }
}