package com.developlife.reviewtwits.mapper;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.user.LoginUserRequest;
import com.developlife.reviewtwits.message.request.user.RegisterUserRequest;
import com.developlife.reviewtwits.message.response.user.UserDetailInfoResponse;
import com.developlife.reviewtwits.message.response.user.UserInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author ghdic
 * @since 2023/02/25
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDetailInfoResponse toDTO(User user);
    UserInfoResponse toDto(User user);
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "profileImage", ignore = true)
    User toEntity(RegisterUserRequest registerUserRequest);


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
