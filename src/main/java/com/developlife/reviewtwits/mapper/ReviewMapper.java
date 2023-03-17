package com.developlife.reviewtwits.mapper;

import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.response.review.DetailReviewResponse;
import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-03-17
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ReviewMapper {

    List<DetailReviewResponse> toDetailReviewResponseList(List<Review> reviews);

    default UserInfoResponse mapUserToUserInfoResponse(User user){
        return new UserInfoResponse(user.getNickname(),user.getAccountId());
    }

    default DetailReviewResponse mapReviewToDetailReviewResponse(Review review){
        return DetailReviewResponse.builder()
                .createdDate(review.getCreatedDate())
                .lastModifiedDate(review.getLastModifiedDate())
                .reviewId(review.getProject().getProjectId())
                .userInfo(mapUserToUserInfoResponse(review.getUser()))
                .content(review.getContent())
                .productUrl(review.getProductUrl())
                .score(review.getScore())
                .reviewImageNameList(review.getReviewImageNameList())
                .build();
    }
}