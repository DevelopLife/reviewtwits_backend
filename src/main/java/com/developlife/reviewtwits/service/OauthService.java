package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.user.AccountIdAlreadyExistsException;
import com.developlife.reviewtwits.exception.user.AccountIdNotFoundException;
import com.developlife.reviewtwits.exception.user.ProviderNotSupportedException;
import com.developlife.reviewtwits.exception.user.RegisterDataNeedException;
import com.developlife.reviewtwits.mapper.UserMapper;
import com.developlife.reviewtwits.message.request.user.RegisterOauthUserRequest;
import com.developlife.reviewtwits.message.response.oauth.OauthUserInfo;
import com.developlife.reviewtwits.repository.UserRepository;
import com.developlife.reviewtwits.type.Gender;
import com.developlife.reviewtwits.type.JwtProvider;
import com.developlife.reviewtwits.utils.oauth.GoogleOAuth2Utils;
import com.developlife.reviewtwits.utils.oauth.KakaoOauth2Utils;
import com.developlife.reviewtwits.utils.oauth.NaverOauth2Utils;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * @author ghdic
 * @since 2023/03/15
 */
@Service
public class OauthService {

    UserRepository userRepository;
    UserMapper userMapper;
    Faker faker;

    public OauthService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.faker = new Faker();
    }

    @Transactional
    public User authenticateToken(OauthUserInfo oauthUserInfo, JwtProvider jwtProvider) {
        Optional<User> optionalUser = userRepository.findByUuid(oauthUserInfo.sub());
        User user = null;
        if(optionalUser.isPresent()) {
            user = optionalUser.get();
        }
        return user;
    }

    @Transactional
    public User registerNeedInfo(String accessToken, RegisterOauthUserRequest registerOauthUserRequest) {
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


        // 이미 회원가입이 된 경우 확인
        userRepository.findByUuid(oauthUserInfo.sub())
                .ifPresent((u) -> {throw new AccountIdAlreadyExistsException("이미 회원가입된 계정입니다");});

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
                .build();
        userRepository.save(user);
        return user;
    }
}
