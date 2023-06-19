package com.developlife.reviewtwits.message.response.review;

import lombok.Builder;

/**
 * @author WhalesBob
 * @since 2023-04-02
 */

public record ReactionResponse (boolean isReacted,
                               int count){

    @Builder
    public ReactionResponse{

    }
}