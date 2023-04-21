package com.developlife.reviewtwits.config.security;

import com.developlife.reviewtwits.exception.user.TokenExpiredException;
import com.developlife.reviewtwits.exception.user.TokenInvalidException;
import com.developlife.reviewtwits.exception.user.AccessDeniedException;
import com.developlife.reviewtwits.type.JwtCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ghdic
 * @since 2023/03/21
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken(request);
        JwtCode code = jwtTokenProvider.validateToken(token);
        switch (code) {
            case ACCESS:
                throw new AccessDeniedException("유저 권한이 부족합니다");
            case EXPIRED:
                throw new TokenExpiredException("토큰이 만료되었습니다.");
            case DENIED:
                throw new TokenInvalidException("토큰이 유효하지 않습니다.");
        }
    }
}
