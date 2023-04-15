package com.developlife.reviewtwits.mapper;

import com.developlife.reviewtwits.entity.Follow;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.response.sns.FollowResultResponse;
import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface FollowMapper {

    @Mapping(target = "reviewCount", ignore = true)
    @Mapping(target = "followers", ignore = true)
    @Mapping(target = "followings", ignore = true)
    UserInfoResponse mapUserToUserInfoResponse(User user);

    default FollowResultResponse toFollowResultResponse(Follow follow) {
        return FollowResultResponse.builder()
                .followId(follow.getFollowId())
                .userInfoResponse(mapUserToUserInfoResponse(follow.getUser()))
                .targetUserInfoResponse(mapUserToUserInfoResponse(follow.getTargetUser()))
                .followBackFlag(follow.isFollowBackFlag())
                .build();
    }
}
