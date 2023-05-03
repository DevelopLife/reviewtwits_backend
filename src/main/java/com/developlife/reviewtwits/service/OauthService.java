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
import com.developlife.reviewtwits.type.JwtProvider;
import com.developlife.reviewtwits.utils.oauth.GoogleOAuth2Utils;
import com.developlife.reviewtwits.utils.oauth.KakaoOauth2Utils;
import com.developlife.reviewtwits.utils.oauth.NaverOauth2Utils;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
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
    public User authenticateToken(OauthUserInfo oauthUserInfo, JwtProvider jwtProvider, HttpServletResponse response) {
        Optional<User> optionalUser = userRepository.findByUuid(oauthUserInfo.sub());
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            String nickname;
            do {
                nickname = faker.name().username();
            } while (nickname.length() > 20 || userRepository.findByNickname(nickname).isPresent());
            user = User.builder()
                    .uuid(oauthUserInfo.sub())
                    .accountId(oauthUserInfo.email())
                    .provider(jwtProvider)
                    .nickname(nickname)
                    .build();
            userRepository.save(user);
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
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

        User user = userRepository.findByUuid(oauthUserInfo.sub())
            .orElseThrow(() -> new AccountIdNotFoundException("정상적인 회원가입 경로가 아닙니다"));

        if(StringUtils.hasText(user.getPhoneNumber()) && StringUtils.hasText(user.getNickname())) {
            throw new AccountIdAlreadyExistsException("이미 입력한 회원가입 정보가 존재합니다");
        }

        userMapper.updateUserFromRegisterOauthUserRequest(registerOauthUserRequest, user);
        userRepository.save(user);
        return user;
    }
}
