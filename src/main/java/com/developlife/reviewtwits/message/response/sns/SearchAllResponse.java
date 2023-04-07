package com.developlife.reviewtwits.message.response.sns;

import lombok.Builder;

import java.util.List;

/**
 * @author ghdic
 * @since 2023/04/05
 */
public record SearchAllResponse(
    List<ItemResponse> itemList,
    List<DetailSnsReviewResponse> reviewList
) {
    @Builder
    public SearchAllResponse {
    }
}
