package com.freedom.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freedom.common.exception.ErrorCode;
import com.freedom.common.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationEntryPointTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Test
    @DisplayName("인증 실패 시 401 Unauthorized 응답을 반환한다")
    void commence_ShouldReturn401Response() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/news");
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException exception = new AuthenticationException("인증 실패") {};
        
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.TOKEN_INVALID);
        String expectedJson = "{\"code\":\"AUTH003\",\"message\":\"유효하지 않은 토큰입니다.\"}";
        
        when(objectMapper.writeValueAsString(any(ErrorResponse.class))).thenReturn(expectedJson);

        // when
        jwtAuthenticationEntryPoint.commence(request, response, exception);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        assertThat(response.getCharacterEncoding()).isEqualTo(StandardCharsets.UTF_8.name());
        assertThat(response.getContentAsString()).isEqualTo(expectedJson);
    }
}
