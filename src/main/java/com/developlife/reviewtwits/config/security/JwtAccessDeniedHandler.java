package com.developlife.reviewtwits.config.security;

import com.developlife.reviewtwits.exception.user.TokenExpiredException;
import com.developlife.reviewtwits.exception.user.TokenInvalidException;
import com.developlife.reviewtwits.exception.user.AccessDeniedException;
import com.developlife.reviewtwits.message.response.ErrorResponse;
import com.developlife.reviewtwits.type.JwtCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private JwtTokenProvider jwtTokenProvider;
    private ObjectMapper objectMapper;

    public JwtAccessDeniedHandler(JwtTokenProvider jwtTokenProvider, ObjectMapper objectMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException) throws IOException {
        String token = jwtTokenProvider.resolveToken(request);
        JwtCode code = jwtTokenProvider.validateToken(token);
        ErrorResponse errorResponse = null;

        response.setContentType("application/json");
        switch (code) {
            case ACCESS:
                response.setStatus(HttpStatus.FORBIDDEN.value());
                errorResponse = ErrorResponse.builder()
                        .errorType("AccessDeniedException")
                        .message("유저 권한이 부족합니다")
                        .fieldName("").build();
                break;
            case EXPIRED:
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                errorResponse = ErrorResponse.builder()
                        .errorType("TokenExpiredException")
                        .message("토큰이 만료되었습니다.")
                        .fieldName("").build();
                break;
            case DENIED:
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                errorResponse = ErrorResponse.builder()
                        .errorType("TokenInvalidException")
                        .message("토큰이 유효하지 않습니다.")
                        .fieldName("").build();
        }
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
