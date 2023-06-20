package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.config.security.JwtTokenProvider;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.user.RegisterOauthUserRequest;
import com.developlife.reviewtwits.message.response.oauth.OauthUserInfo;
import com.developlife.reviewtwits.message.response.user.JwtTokenResponse;
import com.developlife.reviewtwits.service.OauthService;
import com.developlife.reviewtwits.type.JwtProvider;
import com.developlife.reviewtwits.utils.oauth.GoogleOAuth2Utils;
import com.developlife.reviewtwits.utils.oauth.KakaoOauth2Utils;
import com.developlife.reviewtwits.utils.oauth.NaverOauth2Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ghdic
 * @since 2023/03/14
 */
@RestController
@RequestMapping("/oauth")
public class OauthController {

    OauthService oauthService;
    JwtTokenProvider jwtTokenProvider;

    public OauthController(OauthService oauthService, JwtTokenProvider jwtTokenProvider) {
        this.oauthService = oauthService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping({"/kakao","/google", "/naver"})
    public ResponseEntity<Object> login(@RequestHeader("Authorization") String accessToken,
                                        HttpServletRequest request, HttpServletResponse response) {
        String requestURI = request.getRequestURI();
        JwtProvider provider = JwtProvider.getJwtProvider(requestURI);

        OauthUserInfo userInfo = getOauthUserInfo(provider, accessToken);

        User user = oauthService.authenticateToken(userInfo, provider);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(userInfo);
        }
        jwtTokenProvider.setRefreshTokenForClient(response, user);
        return ResponseEntity.ok(jwtTokenProvider.issueJwtTokenResponse(user));
    }

    private OauthUserInfo getOauthUserInfo(JwtProvider provider, String accessToken){
        switch (provider) {
            case KAKAO:
                return KakaoOauth2Utils.getUserInfo(accessToken);
            case GOOGLE:
                return GoogleOAuth2Utils.getUserInfo(accessToken);
            case NAVER:
                return NaverOauth2Utils.getUserInfo(accessToken);
            default:
                throw new IllegalArgumentException("잘못된 요청입니다");
        }
    }

    @PostMapping("/register")
    public JwtTokenResponse register(@RequestHeader("Authorization") String accessToken,
             @RequestBody RegisterOauthUserRequest registerOauthUserRequest, HttpServletResponse response) {
        User user = oauthService.registerNeedInfo(accessToken, registerOauthUserRequest);
        jwtTokenProvider.setRefreshTokenForClient(response, user);

        return jwtTokenProvider.issueJwtTokenResponse(user);
    }
}
