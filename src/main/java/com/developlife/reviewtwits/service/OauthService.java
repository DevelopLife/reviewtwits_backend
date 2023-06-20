package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.FileInfo;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.user.AccountIdAlreadyExistsException;
import com.developlife.reviewtwits.exception.user.ProviderNotSupportedException;
import com.developlife.reviewtwits.mapper.UserMapper;
import com.developlife.reviewtwits.message.request.user.RegisterOauthUserRequest;
import com.developlife.reviewtwits.message.response.oauth.OauthUserInfo;
import com.developlife.reviewtwits.repository.UserRepository;
import com.developlife.reviewtwits.type.Gender;
import com.developlife.reviewtwits.type.JwtProvider;
import com.developlife.reviewtwits.type.ReferenceType;
import com.developlife.reviewtwits.type.UserRole;
import com.developlife.reviewtwits.utils.oauth.GoogleOAuth2Utils;
import com.developlife.reviewtwits.utils.oauth.KakaoOauth2Utils;
import com.developlife.reviewtwits.utils.oauth.NaverOauth2Utils;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

/**
 * @author ghdic
 * @since 2023/03/15
 */
@Service
@RequiredArgsConstructor
public class OauthService {

    final UserRepository userRepository;
    final UserMapper userMapper;
    final FileStoreService fileStoreService;
    Faker faker = new Faker();

    @Transactional
    public User authenticateToken(OauthUserInfo oauthUserInfo, JwtProvider jwtProvider) {
        Optional<User> optionalUser = userRepository.findByUuidAndProvider(oauthUserInfo.sub(), jwtProvider);
        User user = null;
        if(optionalUser.isPresent()) {
            user = optionalUser.get();
        }
        return user;
    }

    @Transactional
    public User registerNeedInfo(String accessToken, RegisterOauthUserRequest registerOauthUserRequest) {
        OauthUserInfo oauthUserInfo = getOauthUserInfo(accessToken, registerOauthUserRequest);

        checkDuplicateInfo(registerOauthUserRequest, oauthUserInfo);

        User user = registerOauthUser(registerOauthUserRequest, oauthUserInfo);
        return user;
    }

    private User registerOauthUser(RegisterOauthUserRequest registerOauthUserRequest, OauthUserInfo oauthUserInfo) {
        String nickname;
        do {
            nickname = faker.name().username();
        } while (nickname.length() > 20 || userRepository.findByNickname(nickname).isPresent());

        Date birthDate = null;
        try {
            if ( registerOauthUserRequest.birthDate() != null ) {
                birthDate = new SimpleDateFormat( "yyyy-MM-dd" ).parse( registerOauthUserRequest.birthDate() );
            }
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }

        User user = User.builder()
                .accountId(oauthUserInfo.email())
                .uuid(oauthUserInfo.sub())
                .nickname(nickname)
                .provider(Enum.valueOf(JwtProvider.class, registerOauthUserRequest.provider()))
                .birthDate(birthDate)
                .phoneNumber(registerOauthUserRequest.phoneNumber())
                .gender(Enum.valueOf(Gender.class, registerOauthUserRequest.gender()))
                .roles(Set.of(UserRole.USER))
                .build();
        userRepository.save(user);
        if(oauthUserInfo.picture() != null) {
            FileInfo fileInfo = fileStoreService.downloadProfileImageFromUrl(oauthUserInfo.picture(), oauthUserInfo.sub(), user.getUserId(), ReferenceType.USER);
            user.setProfileImageUuid(fileInfo.getRealFilename());
        }
        return user;
    }

    private void checkDuplicateInfo(RegisterOauthUserRequest registerOauthUserRequest, OauthUserInfo oauthUserInfo) {
        // 이미 회원가입이 된 경우 확인
        userRepository.findByUuid(oauthUserInfo.sub())
                .ifPresent((u) -> {throw new AccountIdAlreadyExistsException("이미 회원가입된 계정입니다");});
        // 이메일 중복 확인
        userRepository.findByAccountId(oauthUserInfo.email())
                .ifPresent((u) -> {throw new AccountIdAlreadyExistsException("이미 회원가입된 이메일입니다");});
        // 전화번호 중복 확인
        if(userRepository.existsByPhoneNumber(registerOauthUserRequest.phoneNumber())) {
            throw new AccountIdAlreadyExistsException("이미 회원가입된 전화번호입니다");
        }
    }

    private static OauthUserInfo getOauthUserInfo(String accessToken, RegisterOauthUserRequest registerOauthUserRequest) {
        OauthUserInfo oauthUserInfo;
        JwtProvider jwtProvider = JwtProvider.valueOf(registerOauthUserRequest.provider());
        switch (jwtProvider) {
            case KAKAO:
                oauthUserInfo = KakaoOauth2Utils.getUserInfo(accessToken);
                break;
            case GOOGLE:
                oauthUserInfo = GoogleOAuth2Utils.getUserInfo(accessToken);
                break;
            case NAVER:
                oauthUserInfo = NaverOauth2Utils.getUserInfo(accessToken);
                break;
            default:
                throw new ProviderNotSupportedException("지원하지 않는 provider 입니다");
        }
        return oauthUserInfo;
    }
}
