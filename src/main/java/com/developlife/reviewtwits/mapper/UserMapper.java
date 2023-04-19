package com.developlife.reviewtwits.mapper;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.user.RegisterOauthUserRequest;
import com.developlife.reviewtwits.message.request.user.RegisterUserInfoRequest;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.message.response.email.FindIdsEmailResponse;
import com.developlife.reviewtwits.message.response.user.UserDetailInfoResponse;
import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import org.mapstruct.*;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author ghdic
 * @since 2023/02/25
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

    String imageUrl = "/request-images/";

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "birthDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "projectList", ignore = true)
    @Mapping(target = "nickname", ignore = true)
    @Mapping(target = "introduceText", ignore = true)
    @Mapping(target = "detailIntroduce", ignore = true)
    User toUser(RegisterUserRequest registerUserRequest);


    FindIdsEmailResponse toFindIdsEmailResponse(User user);
    List<FindIdsEmailResponse> toFindIdsEmailResponseList(List<User> users);
    List<UserInfoResponse> toUserInfoResponseList(List<User> users);

    @Mapping(target = "accountPw", ignore = true)
    @Mapping(target = "projectList", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "nickname", ignore = true)
    @Mapping(target = "introduceText", ignore = true)
    @Mapping(target = "profileImageUuid", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    void updateUserFromRegisterOauthUserRequest(RegisterOauthUserRequest registerOauthUserRequest, @MappingTarget User user);


    @Mapping(target = "profileImageUuid", ignore = true)
    void updateUserFromRegisterUserInfoRequest(RegisterUserInfoRequest registerUserInfoRequest, @MappingTarget User user);

    default UserInfoResponse toUserInfoResponse(User user, int followers, int followings, int reviewCount){
        return UserInfoResponse.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .accountId(user.getAccountId())
                .introduceText(user.getIntroduceText())
                .profileImageUrl(imageUrl+user.getProfileImageUuid())
                .detailIntroduce(user.getDetailIntroduce())
                .reviewCount(reviewCount)
                .followers(followers)
                .followings(followings)
                .build();
    }


    default UserInfoResponse toUserInfoResponse(User user){
        return UserInfoResponse.builder()
                .nickname(user.getNickname())
                .accountId(user.getAccountId())
                .introduceText(user.getIntroduceText())
                .profileImageUrl(imageUrl+user.getProfileImageUuid())
                .detailIntroduce(user.getDetailIntroduce())
                .reviewCount(0)
                .followers(0)
                .followings(0)
                .build();
    }

    default UserDetailInfoResponse toUserDetailInfoResponse(User user){
        return new UserDetailInfoResponse(
                user.getNickname(),
                user.getAccountId(),
                new SimpleDateFormat("yyyy-MM-dd").format(user.getBirthDate()),
                user.getPhoneNumber(),
                user.getGender(),
                user.getProvider().toString(),
                user.getUuid(),
                imageUrl+user.getProfileImageUuid(),
                user.getIntroduceText()
        );
    }
}
