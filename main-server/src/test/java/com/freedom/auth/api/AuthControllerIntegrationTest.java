package com.freedom.auth.api;

import com.freedom.auth.api.request.LoginRequest;
import com.freedom.auth.api.request.SignUpRequest;
import com.freedom.auth.api.response.LoginResponse;
import com.freedom.auth.api.response.SignUpResponse;
import com.freedom.auth.domain.User;
import com.freedom.auth.domain.UserRole;
import com.freedom.auth.domain.UserStatus;
import com.freedom.auth.infra.RefreshTokenJpaRepository;
import com.freedom.auth.infra.UserJpaRepository;
import com.freedom.common.test.TestContainerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuthController API 통합 테스트")
class AuthControllerIntegrationTest extends TestContainerConfig {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private RefreshTokenJpaRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String testPassword = "testpass123!";

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User createTestUser(String email) {
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(testPassword))
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();
        return userRepository.save(user);
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp_Success() {
        SignUpRequest request = new SignUpRequest("newuser@example.com", "newpass123!");

        webTestClient.post()
                .uri("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SignUpResponse.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getEmail()).isEqualTo("newuser@example.com");
                    assertThat(response.getRole()).isEqualTo("USER");
                    assertThat(response.getStatus()).isEqualTo("ACTIVE");
                    assertThat(response.getCharacterCreated()).isFalse();
                    assertThat(response.getCreatedAt()).isNotNull();
                });
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 이메일")
    void signUp_Fail_DuplicateEmail() {
        createTestUser("existing@example.com");
        SignUpRequest request = new SignUpRequest("existing@example.com", "newpass123!");

        webTestClient.post()
                .uri("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.code").isEqualTo("USER002")
                .jsonPath("$.message").isEqualTo("이미 사용 중인 이메일입니다.");
    }

    @Test
    @DisplayName("회원가입 실패 - 잘못된 이메일 형식")
    void signUp_Fail_InvalidEmail() {
        SignUpRequest request = new SignUpRequest("invalid-email", "newpass123!");

        webTestClient.post()
                .uri("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("VALIDATION001")
                .jsonPath("$.message").isEqualTo("입력값 검증에 실패했습니다.")
                .jsonPath("$.errors").isArray()
                .jsonPath("$.errors[0].field").isEqualTo("email")
                .jsonPath("$.errors[0].code").isEqualTo("Email")
                .jsonPath("$.errors[0].message").isEqualTo("올바른 이메일 형식이어야 합니다.");
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        User testUser = createTestUser("existing@example.com");
        LoginRequest request = new LoginRequest("existing@example.com", testPassword);

        webTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponse.class)
                .value(response -> {
                    assertThat(response.getAccessToken()).isNotBlank();
                    assertThat(response.getRefreshToken()).isNotBlank();
                    assertThat(response.getTokenType()).isEqualTo("Bearer");
                    assertThat(response.getExpiresIn()).isPositive();
                    assertThat(response.getUser().getUserId()).isEqualTo(testUser.getId());
                    assertThat(response.getUser().getEmail()).isEqualTo("existing@example.com");
                    assertThat(response.getUser().getRole()).isEqualTo("USER");
                    assertThat(response.getUser().getStatus()).isEqualTo("ACTIVE");
                    assertThat(response.getUser().getCharacterCreated()).isFalse();
                });
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void login_Fail_UserNotFound() {
        LoginRequest request = new LoginRequest("notfound@example.com", testPassword);

        webTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("USER001")
                .jsonPath("$.message").isEqualTo("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_Fail_InvalidPassword() {
        createTestUser("existing@example.com");
        LoginRequest request = new LoginRequest("existing@example.com", "wrongpassword");

        webTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("USER003")
                .jsonPath("$.message").isEqualTo("비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("로그인 실패 - 탈퇴한 사용자")
    void login_Fail_WithdrawnUser() {
        User user = createTestUser("withdrawn@example.com");
        user.changeStatus(UserStatus.WITHDRAWN);
        userRepository.save(user);
        
        LoginRequest request = new LoginRequest("withdrawn@example.com", testPassword);

        webTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.code").isEqualTo("USER004")
                .jsonPath("$.message").isEqualTo("탈퇴한 사용자입니다.");
    }
}
