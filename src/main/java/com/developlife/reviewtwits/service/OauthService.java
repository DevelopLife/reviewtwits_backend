package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.config.security.JwtTokenProvider;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.user.AccountIdAlreadyExistsException;
import com.developlife.reviewtwits.exception.user.AccountIdNotFoundException;
import com.developlife.reviewtwits.exception.user.ProviderNotSupportedException;
import com.developlife.reviewtwits.exception.user.RegisterDataNeedException;
import com.developlife.reviewtwits.mapper.UserMapper;
import com.developlife.reviewtwits.message.request.user.RegisterOauthUserRequest;
import com.developlife.reviewtwits.message.response.oauth.OauthUserInfo;
import com.developlife.reviewtwits.message.response.user.JwtTokenResponse;
import com.developlife.reviewtwits.repository.UserRepository;
import com.developlife.reviewtwits.type.JwtProvider;
import com.developlife.reviewtwits.utils.oauth.GoogleOAuth2Utils;
import com.developlife.reviewtwits.utils.oauth.KakaoOauth2Utils;
import com.developlife.reviewtwits.utils.oauth.NaverOauth2Utils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * @author ghdic
 * @since 2023/03/15
 */
@Service
public class OauthService {

    UserRepository userRepository;
    UserMapper userMapper;

    public OauthService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public User authenticateToken(OauthUserInfo oauthUserInfo, JwtProvider jwtProvider) {
        Optional<User> optionalUser = userRepository.findByUuid(oauthUserInfo.sub());
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            if(!StringUtils.hasText(user.getPhoneNumber()) || !StringUtils.hasText(user.getNickname())) {
                throw new RegisterDataNeedException("추가 회원가입 입력정보가 필요합니다");
            }
        } else {
            user = User.builder()
                    .uuid(oauthUserInfo.sub())
                    .accountId(oauthUserInfo.email())
                    .provider(jwtProvider)
                    .build();
            userRepository.save(user);
            throw new RegisterDataNeedException("추가 회원가입 입력정보가 필요합니다");
        }

        return user;
    }

    @Transactional
    public User registerNeedInfo(String accessToken, RegisterOauthUserRequest registerOauthUserRequest) {
        OauthUserInfo oauthUserInfo;
        switch (registerOauthUserRequest.provider()) {
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
