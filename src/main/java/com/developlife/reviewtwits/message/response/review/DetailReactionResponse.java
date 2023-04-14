package com.developlife.reviewtwits.message.response.review;

import com.developlife.reviewtwits.type.ReactionType;
import lombok.Builder;

public record DetailReactionResponse(
        long reactionId,
        long userId,
        long reviewId,
        ReactionType reactionType
) {

    @Builder
    public DetailReactionResponse {
    }
}
