package com.freedom.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freedom.common.exception.ErrorCode;
import com.freedom.common.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * JWT 인증 실패 시 401 Unauthorized 응답을 반환하는 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, 
                        AuthenticationException authException) throws IOException, ServletException {
        
        log.warn("인증되지 않은 사용자의 접근: {} {}", request.getMethod(), request.getRequestURI());
        
        // 401 Unauthorized 응답 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        
        // 에러 응답 생성
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.TOKEN_INVALID);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);
    }
}
