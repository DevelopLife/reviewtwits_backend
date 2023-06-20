package com.developlife.reviewtwits.type;

/**
 * @author ghdic
 * @since 2023/03/15
 */
public enum JwtProvider {
    GOOGLE,
    KAKAO,
    NAVER,
    REVIEWTWITS;

    public static JwtProvider getJwtProvider(String requestURI){
        switch (requestURI) {
            case "/oauth/kakao":
                return JwtProvider.KAKAO;
            case "/oauth/google":
                return JwtProvider.GOOGLE;
            case "/oauth/naver":
                return JwtProvider.NAVER;
            default:
                throw new IllegalArgumentException("잘못된 요청입니다");
        }
    }
}
