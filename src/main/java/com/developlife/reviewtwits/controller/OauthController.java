package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.message.response.oauth.GoogleUserInfo;
import com.developlife.reviewtwits.message.response.oauth.KakaoUserInfo;
import com.developlife.reviewtwits.message.response.oauth.NaverUserInfo;
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

    @PostMapping("/kakao")
    public KakaoUserInfo kakao(@RequestHeader("Authorization") String accessToken) {
        KakaoUserInfo kakaoUserInfo = KakaoOauth2Utils.getUserInfo(accessToken);
        return kakaoUserInfo;
    }

    @PostMapping("/google")
    public GoogleUserInfo google(@RequestHeader("Authorization") String accessToken) {
        GoogleUserInfo googleUserInfo = GoogleOAuth2Utils.getUserInfo(accessToken);
        return googleUserInfo;
    }

    @PostMapping("/naver")
    public NaverUserInfo naver(@RequestHeader("Authorization") String accessToken) {
        NaverUserInfo naverUserInfo = NaverOauth2Utils.getUserInfo(accessToken);
        return naverUserInfo;
    }
}
