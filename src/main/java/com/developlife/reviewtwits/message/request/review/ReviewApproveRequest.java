package com.developlife.reviewtwits.message.request.review;

import com.developlife.reviewtwits.message.annotation.review.ReviewApprove;
import lombok.Builder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author WhalesBob
 * @since 2023-05-19
 */

public record ReviewApproveRequest(@NotNull(message = "리뷰 아이디는 필수 입력 값입니다.")
                                   @Min(value = 1, message = "리뷰 아이디는 최소 1 이상의 숫자로 입력해야 합니다.")
                                   Long reviewId,
                                   @ReviewApprove
                                   String approveType) {
    @Builder
    public ReviewApproveRequest{
    }
}