package com.developlife.reviewtwits.mapper;

import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.response.review.CommentResponse;
import com.developlife.reviewtwits.message.response.review.DetailShoppingMallReviewResponse;
import com.developlife.reviewtwits.message.response.review.DetailSnsReviewResponse;
import com.developlife.reviewtwits.message.response.review.ReactionResponse;
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

    List<DetailShoppingMallReviewResponse> toDetailReviewResponseList(List<Review> reviews);
    List<CommentResponse> toCommentResponseList(List<Comment> comments);

    default UserInfoResponse mapUserToUserInfoResponse(User user){
        return new UserInfoResponse(user.getNickname(),user.getAccountId(),user.getIntroduceText(),user.getProfileImage());
    }

    default DetailShoppingMallReviewResponse mapReviewToDetailReviewResponse(Review review){
        return DetailShoppingMallReviewResponse.builder()
                .createdDate(review.getCreatedDate())
                .lastModifiedDate(review.getLastModifiedDate())
                .reviewId(review.getReviewId())
                .userInfo(mapUserToUserInfoResponse(review.getUser()))
                .projectId(review.getProject().getProjectId())
                .content(review.getContent())
                .productUrl(review.getProductUrl())
                .score(review.getScore())
                .reviewImageNameList(review.getReviewImageNameList())
                .exist(review.isExist())
                .build();
    }
}