package com.developlife.reviewtwits.config.security;

import com.developlife.reviewtwits.exception.user.TokenExpiredException;
import com.developlife.reviewtwits.exception.user.TokenInvalidException;
import com.developlife.reviewtwits.exception.user.AccessDeniedException;
import com.developlife.reviewtwits.type.JwtCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author ghdic
 * @since 2023/04/21
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken(request);
        JwtCode code = jwtTokenProvider.validateToken(token);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Map<String, String> errorMsg = new LinkedHashMap<>();

        switch (code) {
            case ACCESS:
                errorMsg.put("message", "유저 권한이 부족합니다.");
                errorMsg.put("errorType", "AccessDeniedException");
                //throw new AccessDeniedException("유저 권한이 부족합니다");
            case EXPIRED:
                errorMsg.put("message", "토큰이 만료되었습니다.");
                errorMsg.put("errorType", "TokenExpiredException");
                //throw new TokenExpiredException("토큰이 만료되었습니다.");
            case DENIED:
                errorMsg.put("message", "토큰이 유효하지 않습니다.");
                errorMsg.put("errorType", "TokenInvalidException");
                //throw new TokenInvalidException("토큰이 유효하지 않습니다.");
        }
        errorMsg.put("fieldName", "");
        response.getWriter().write(objectMapper.writeValueAsString(errorMsg));
    }
}
