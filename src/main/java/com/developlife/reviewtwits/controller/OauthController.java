package com.developlife.reviewtwits.controller;

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

/**
 * @author ghdic
 * @since 2023/03/14
 */
@RestController
@RequestMapping("/oauth")
public class OauthController {

    OauthService oauthService;

    public OauthController(OauthService oauthService) {
        this.oauthService = oauthService;
    }

    @PostMapping("/kakao")
    public JwtTokenResponse kakao(@RequestHeader("Authorization") String accessToken) {
        KakaoUserInfo kakaoUserInfo = KakaoOauth2Utils.getUserInfo(accessToken);
        return oauthService.authenticateToken(kakaoUserInfo, JwtProvider.KAKAO);
    }

    @PostMapping("/google")
    public JwtTokenResponse google(@RequestHeader("Authorization") String accessToken) {
        GoogleUserInfo googleUserInfo = GoogleOAuth2Utils.getUserInfo(accessToken);
        return oauthService.authenticateToken(googleUserInfo, JwtProvider.GOOGLE);
    }

    @PostMapping("/naver")
    public JwtTokenResponse naver(@RequestHeader("Authorization") String accessToken) {
        NaverUserInfo naverUserInfo = NaverOauth2Utils.getUserInfo(accessToken);
        return oauthService.authenticateToken(naverUserInfo, JwtProvider.NAVER);
    }

    @PostMapping("/register")
    public JwtTokenResponse register(@RequestHeader("Authorization") String accessToken,
             @RequestBody RegisterOauthUserRequest registerOauthUserRequest) {
        return oauthService.registerNeedInfo(accessToken, registerOauthUserRequest);
    }
}
