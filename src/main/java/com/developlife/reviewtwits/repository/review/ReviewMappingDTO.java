package com.developlife.reviewtwits.repository.review;

import com.developlife.reviewtwits.entity.Reaction;
import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.ReviewScrap;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * @author WhalesBob
 * @since 2023-04-17
 */

@Setter
@Getter
public class ReviewMappingDTO {
    private Review review;
    private Set<String> reviewImageNameSet;
    private Set<Reaction> reactionSet;
    private Set<ReviewScrap> reviewScrap;

    @QueryProjection
    public ReviewMappingDTO(Review review, Set<String> reviewImageNameSet, Set<Reaction> reactionSet, Set<ReviewScrap> reviewScrap) {
        this.review = review;
        this.reviewImageNameSet = reviewImageNameSet;
        this.reactionSet = reactionSet;
        this.reviewScrap = reviewScrap;
    }
}