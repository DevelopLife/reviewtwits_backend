package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.config.security.JwtTokenProvider;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.user.RegisterOauthUserRequest;
import com.developlife.reviewtwits.message.response.oauth.GoogleUserInfo;
import com.developlife.reviewtwits.message.response.oauth.KakaoUserInfo;
import com.developlife.reviewtwits.message.response.oauth.NaverUserInfo;
import com.developlife.reviewtwits.message.response.user.JwtTokenResponse;
import com.developlife.reviewtwits.service.OauthService;
import com.developlife.reviewtwits.type.JwtProvider;
import com.developlife.reviewtwits.utils.oauth.GoogleOAuth2Utils;
import com.developlife.reviewtwits.utils.oauth.KakaoOauth2Utils;
import com.developlife.reviewtwits.utils.oauth.NaverOauth2Utils;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/kakao")
    public JwtTokenResponse kakao(@RequestHeader("Authorization") String accessToken,
                                  HttpServletResponse response) {
        KakaoUserInfo kakaoUserInfo = KakaoOauth2Utils.getUserInfo(accessToken);
        User user = oauthService.authenticateToken(kakaoUserInfo, JwtProvider.KAKAO, response);
        jwtTokenProvider.setRefreshTokenForClient(response, user);

        return jwtTokenProvider.issueJwtTokenResponse(user);
    }

    @PostMapping("/google")
    public JwtTokenResponse google(@RequestHeader("Authorization") String accessToken,
                                   HttpServletResponse response) {
        GoogleUserInfo googleUserInfo = GoogleOAuth2Utils.getUserInfo(accessToken);
        User user = oauthService.authenticateToken(googleUserInfo, JwtProvider.GOOGLE, response);
        jwtTokenProvider.setRefreshTokenForClient(response, user);

        return jwtTokenProvider.issueJwtTokenResponse(user);
    }

    @PostMapping("/naver")
    public JwtTokenResponse naver(@RequestHeader("Authorization") String accessToken,
                                  HttpServletResponse response) {
        NaverUserInfo naverUserInfo = NaverOauth2Utils.getUserInfo(accessToken);
        User user = oauthService.authenticateToken(naverUserInfo, JwtProvider.NAVER, response);
        jwtTokenProvider.setRefreshTokenForClient(response, user);

        return jwtTokenProvider.issueJwtTokenResponse(user);
    }

    @PostMapping("/register")
    public JwtTokenResponse register(@RequestHeader("Authorization") String accessToken,
             @RequestBody RegisterOauthUserRequest registerOauthUserRequest, HttpServletResponse response) {
        User user = oauthService.registerNeedInfo(accessToken, registerOauthUserRequest);
        jwtTokenProvider.setRefreshTokenForClient(response, user);

        return jwtTokenProvider.issueJwtTokenResponse(user);
    }
}
