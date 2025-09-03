package com.freedom.common.config;

import com.freedom.common.security.JwtAuthenticationEntryPoint;
import com.freedom.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (JWT 사용으로 불필요)
                .csrf(AbstractHttpConfigurer::disable)
                // CORS 설정 (필요시 추가)
                .cors(AbstractHttpConfigurer::disable)
                // 세션 사용하지 않음
                .sessionManagement(session -> 
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // URL별 인가 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 불필요한 엔드포인트
                        .requestMatchers(
                                "/api/auth/sign-up",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                // 관리자 로그인 페이지 및 정적 리소스
                                "/admin/login",
                                "/admin/api/auth/login",
                                // JSP 파일 접근 허용
                                "/WEB-INF/views/**",
                                "/static/**"
                        ).permitAll()
                        // 관리자 페이지는 ADMIN 역할 필요
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 나머지는 모두 인증 필요
                        .anyRequest().authenticated()
                )
                // 인증 실패 시 예외 처리
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                // JWT 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
