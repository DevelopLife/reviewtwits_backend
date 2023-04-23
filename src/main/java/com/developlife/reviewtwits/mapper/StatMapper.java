package com.developlife.reviewtwits.mapper;

import com.developlife.reviewtwits.entity.StatInfo;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.response.statistics.SaveStatResponse;
import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface StatMapper {

    @Mapping(target = "profileImageUrl", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    @Mapping(target = "followers", ignore = true)
    @Mapping(target = "followings", ignore = true)
    UserInfoResponse mapUserToUserInfoResponse(User user);

    default SaveStatResponse mapStatInfoToSaveStatResponse(StatInfo statInfo){
        return SaveStatResponse.builder()
                .statId(statInfo.getStatId())
                .userInfo(mapUserToUserInfoResponse(statInfo.getUser()))
                .createdDate(statInfo.getCreatedDate().toLocalDate().toString())
                .inflowUrl(statInfo.getInflowUrl())
                .productUrl(statInfo.getProductUrl())
                .productId(statInfo.getProduct().getProductId())
                .projectId(statInfo.getProject().getProjectId())
                .deviceInfo(statInfo.getDevice().toString())
                .build();
    }
}
