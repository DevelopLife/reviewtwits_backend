package com.developlife.reviewtwits.mapper;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.user.RegisterOauthUserRequest;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.message.response.email.FindIdsEmailResponse;
import com.developlife.reviewtwits.message.response.user.UserDetailInfoResponse;
import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import org.mapstruct.*;

import java.util.List;

/**
 * @author ghdic
 * @since 2023/02/25
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

    @Mapping(target = "birthDate", dateFormat = "yyyy-MM-dd")
    UserDetailInfoResponse toUserDetailInfoResponse(User user);
    UserInfoResponse toUserInfoResponse(User user);
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "profileImage", ignore = true)
    @Mapping(target = "birthDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "projectList", ignore = true)
    User toUser(RegisterUserRequest registerUserRequest);


    FindIdsEmailResponse toFindIdsEmailResponse(User user);
    List<FindIdsEmailResponse> toFindIdsEmailResponseList(List<User> users);
    List<UserInfoResponse> toUserInfoResponseList(List<User> users);

    @Mapping(target = "accountPw", ignore = true)
    @Mapping(target = "projectList", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "provider", ignore = true)
    void updateUserFromRegisterOauthUserRequest(RegisterOauthUserRequest registerOauthUserRequest, @MappingTarget User user);

//     List<LoginUserRequest> map(List<User> users);

//    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);
//
//    @Mapping(target = "id", ignore = true) // 1
//    @Mapping(source = "password", target = "password", qualifiedByName = "encryptPassword")
//    Member toEntity(MemberJoinDto dto);
//
//    @Mapping(source = "email", target = "name") // 3
//    MemberDto toDto(Member member);
//
//    @Named("encryptPassword") // 2
//    default String encryptPassword(String password) {
//        return new BCryptPasswordEncoder().encode(password);
//    }
}
