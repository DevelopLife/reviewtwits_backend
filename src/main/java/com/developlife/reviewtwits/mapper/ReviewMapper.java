package com.developlife.reviewtwits.mapper;

import com.developlife.reviewtwits.entity.Comment;
import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.response.review.CommentResponse;
import com.developlife.reviewtwits.message.response.review.DetailShoppingMallReviewResponse;
import com.developlife.reviewtwits.message.response.review.ReactionResponse;
import com.developlife.reviewtwits.message.response.sns.DetailSnsReviewResponse;
import com.developlife.reviewtwits.message.response.sns.SnsReviewResponse;
import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

/**
 * @author WhalesBob
 * @since 2023-03-17
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ReviewMapper {

    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);
    List<DetailShoppingMallReviewResponse> toDetailReviewResponseList(List<Review> reviews);
    List<CommentResponse> toCommentResponseList(List<Comment> comments);
    List<SnsReviewResponse> toSnsReviewResponseList(List<Review> reviews);

    @Mapping(target = "reviewCount", ignore = true)
    @Mapping(target = "followers", ignore = true)
    @Mapping(target = "followings", ignore = true)
    UserInfoResponse mapUserToUserInfoResponse(User user);

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

    @Named(value = "toDetailSnsReviewResponse")
    default DetailSnsReviewResponse toDetailSnsReviewResponse(Review review,
                                                              Map<String, ReactionResponse> reactionResponses, boolean isScrapped){
        return DetailSnsReviewResponse.builder()
                .createdDate(review.getCreatedDate())
                .lastModifiedDate(review.getLastModifiedDate())
                .reviewId(review.getReviewId())
                .productName(review.getProductName())
                .userInfo(mapUserToUserInfoResponse(review.getUser()))
                .content(review.getContent())
                .productUrl(review.getProductUrl())
                .score(review.getScore())
                .reviewImageNameList(review.getReviewImageNameList())
                .commentCount(review.getCommentCount())
                .reactionResponses(reactionResponses)
                .isScrapped(isScrapped)
                .build();
    }

    default CommentResponse toCommentResponse(Comment comment){
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .parentCommentId(comment.getCommentGroup().getCommentId())
                .userInfo(mapUserToUserInfoResponse(comment.getUser()))
                .build();
    }

    default SnsReviewResponse toSnsReviewResponse(Review review){
        return SnsReviewResponse.builder()
                .reviewId(review.getReviewId())
                .userInfo(mapUserToUserInfoResponse(review.getUser()))
                .reviewImageNameList(review.getReviewImageNameList())
                .commentCount(review.getCommentCount())
                .reactionCount(review.getReactionCount())
                .build();
    }
}