package com.developlife.reviewtwits.message.response.review;

import com.developlife.reviewtwits.type.ReactionType;
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