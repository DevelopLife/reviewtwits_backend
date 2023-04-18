package com.developlife.reviewtwits.repository.review;

import com.developlife.reviewtwits.entity.Reaction;
import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.ReviewScrap;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

/**
 * @author WhalesBob
 * @since 2023-04-17
 */

@Setter
@Getter
public class ReviewMappingDTO {
    private Review review;
    private List<String> reviewImageNameList;
    private List<Reaction> reactionList;
    private Set<ReviewScrap> reviewScrap;

    @QueryProjection
    public ReviewMappingDTO(Review review, List<String> reviewImageNameList, List<Reaction> reactionList, Set<ReviewScrap> reviewScrap) {
        this.review = review;
        this.reviewImageNameList = reviewImageNameList;
        this.reactionList = reactionList;
        this.reviewScrap = reviewScrap;
    }
}