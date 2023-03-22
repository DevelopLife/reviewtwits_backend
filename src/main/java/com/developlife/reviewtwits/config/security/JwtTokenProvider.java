package com.developlife.reviewtwits.config.security;

import com.developlife.reviewtwits.controller.UserController;
import com.developlife.reviewtwits.entity.RefreshToken;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.user.AccountIdNotFoundException;
import com.developlife.reviewtwits.exception.user.TokenInvalidException;
import com.developlife.reviewtwits.message.response.user.JwtTokenResponse;
import com.developlife.reviewtwits.repository.RefreshTokenRepository;
import com.developlife.reviewtwits.repository.UserRepository;
import com.developlife.reviewtwits.type.JwtCode;
import com.developlife.reviewtwits.type.UserRole;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

/**
 * @author ghdic
 * @since 2023/02/19
 */
@Component
@Slf4j
public class JwtTokenProvider {
    private final UserRepository userRepository;

    private final String tokenType = "Bearer";
    private final String prefix = "Bearer ";
    private String secretKey;

    public static long tokenValidTime = 60 * 60 * 1000L; // 1시간
    public static long refreshTokenValidTime = 30 * 60 * 60 * 24 * 1000L; // 30일
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtTokenProvider(@Value("${jwt.secret.key}") String secretKey, UserDetailsService userDetailsService, RefreshTokenRepository refreshTokenRepository,
                            UserRepository userRepository) {
        this.secretKey = secretKey;
        this.userDetailsService = userDetailsService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT 토큰 생성
    public String issueAccessToken(String userPk, Set<UserRole> roles) {
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload 에 저장되는 정보단위
        claims.put("roles", roles); // 정보는 key / value 쌍으로 저장된다.
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + tokenValidTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();
        return token;
    }

    @Transactional
    public String issueRefreshToken(String userPk) {
        Claims claims = Jwts.claims().setSubject(userPk);
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        refreshTokenRepository.findByAccountId(userPk).ifPresentOrElse(
                refreshToken -> {
                    refreshToken.setToken(token);
                    refreshTokenRepository.save(refreshToken);
                },
                () -> {
                    RefreshToken refreshToken = RefreshToken.builder()
                            .token(token)
                            .accountId(userPk)
                            .build();
                    refreshTokenRepository.save(refreshToken);
                }
        );
        return token;
    }

    public JwtTokenResponse issueJwtTokenResponse(User user) {
        String accessToken = issueAccessToken(user.getAccountId(), user.getRoles());
        return JwtTokenResponse.builder()
                .accessToken(accessToken)
                .tokenType(tokenType)
                .provider(user.getProvider())
                .build();
    }


    public String reissueAccessToken(String refreshToken) {
        // refresh token 유효성 검사
        Authentication authentication = getAuthentication(refreshToken);
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken).orElseThrow(
            () -> new TokenInvalidException("refresh token이 유효하지 않습니다.")
        );
        if(this.validateToken(refreshToken) == JwtCode.ACCESS) {
            // access token 재발급
            User user = userRepository.findByAccountId(authentication.getName())
                    .orElseThrow(() -> new AccountIdNotFoundException(authentication.getName() + " 사용자를 찾을 수 없습니다."));

            return issueAccessToken(user.getAccountId(), user.getRoles());
        }

        throw new TokenInvalidException("refresh token이 유효하지 않습니다.");
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // Request의 Header에서 token 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN값'
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    // 토큰의 유효성 + 만료일자 확인
    public JwtCode validateToken(String jwtToken) {
        if (jwtToken == null) {
            return JwtCode.DENIED;
        }

        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return JwtCode.ACCESS;
        } catch (ExpiredJwtException e) {
            return JwtCode.EXPIRED;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("잘못된 JWT 서명입니다.");
        }
        return JwtCode.DENIED;
    }

    // prefix를 제거한다
    public String removePrefix(String token) {
        if (token != null && token.startsWith(prefix)) {
            return token.substring(prefix.length());
        }
        return null;
    }

    public void setRefreshTokenForClient(HttpServletResponse response, User user) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", issueRefreshToken(user.getAccountId()))
            .maxAge(refreshTokenValidTime / 1000)
            // 보안 설정
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("None")
            .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void removeRefreshTokenForClient(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", null)
            .maxAge(0)
            // 보안 설정
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("None")
            .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
