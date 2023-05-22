package com.developlife.reviewtwits.type.review;

import com.developlife.reviewtwits.exception.review.SortDirectionException;
import org.springframework.data.domain.Sort;

public enum SortDirectionFilter {
    NEWEST,
    OLDEST;

    public static Sort getSortFromDirection(String direction, String property) {
        if (direction.equals(NEWEST.name())) {
            return Sort.by(property).descending();
        }
        if (direction.equals(OLDEST.name())){
            return Sort.by(property).ascending();
        }
        throw new SortDirectionException("리뷰 오름/내림차순 기준 요청이 NEWEST, OLDEST 중 하나로 입력되지 않았습니다.");
    }
}
