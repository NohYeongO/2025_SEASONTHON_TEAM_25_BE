package com.freedom.common.security;

import com.freedom.common.exception.custom.TokenExpiredException;
import com.freedom.common.exception.custom.TokenInvalidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtProvider 단위 테스트")
class JwtProviderTest {

    private JwtProvider jwtProvider;
    private final String secret = "mySecretKeyForTestingPurposesOnly1234567890";
    private final long accessTokenExpiration = 3600000L; // 1시간
    private final long refreshTokenExpiration = 604800000L; // 7일

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(secret, accessTokenExpiration, refreshTokenExpiration);
    }

    @Test
    @DisplayName("Access Token 생성 성공")
    void createAccessToken_Success() {
        // given
        Long userId = 1L;

        // when
        String token = jwtProvider.createAccessToken(userId);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
        assertThat(jwtProvider.getUserId(token)).isEqualTo(userId);
        assertThat(jwtProvider.getTokenType(token)).isEqualTo("ACCESS");
        assertThat(jwtProvider.isAccessToken(token)).isTrue();
        assertThat(jwtProvider.isRefreshToken(token)).isFalse();
    }

    @Test
    @DisplayName("Refresh Token 생성 성공")
    void createRefreshToken_Success() {
        // given
        Long userId = 1L;

        // when
        String token = jwtProvider.createRefreshToken(userId);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
        assertThat(jwtProvider.getUserId(token)).isEqualTo(userId);
        assertThat(jwtProvider.getTokenType(token)).isEqualTo("REFRESH");
        assertThat(jwtProvider.isRefreshToken(token)).isTrue();
        assertThat(jwtProvider.isAccessToken(token)).isFalse();
    }

    @Test
    @DisplayName("토큰에서 사용자 ID 추출 성공")
    void getUserId_Success() {
        // given
        Long userId = 123L;
        String token = jwtProvider.createAccessToken(userId);

        // when
        Long extractedUserId = jwtProvider.getUserId(token);

        // then
        assertThat(extractedUserId).isEqualTo(userId);
    }

    @Test
    @DisplayName("토큰 타입 추출 성공")
    void getTokenType_Success() {
        // given
        Long userId = 1L;
        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        // when & then
        assertThat(jwtProvider.getTokenType(accessToken)).isEqualTo("ACCESS");
        assertThat(jwtProvider.getTokenType(refreshToken)).isEqualTo("REFRESH");
    }

    @Test
    @DisplayName("유효한 토큰 검증 성공")
    void validateToken_Success() {
        // given
        Long userId = 1L;
        String token = jwtProvider.createAccessToken(userId);

        // when & then
        assertThat(jwtProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("유효하지 않은 토큰 검증 실패")
    void validateToken_Fail_InvalidToken() {
        // given
        String invalidToken = "invalid.jwt.token";

        // when & then
        assertThat(jwtProvider.validateToken(invalidToken)).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰 검증 - TokenExpiredException 발생")
    void validateToken_Fail_ExpiredToken() {
        // given
        JwtProvider shortExpirationProvider = new JwtProvider(secret, 1L, 1L); // 1ms 만료
        Long userId = 1L;
        String token = shortExpirationProvider.createAccessToken(userId);

        // 토큰이 만료될 때까지 대기
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // when & then
        assertThatThrownBy(() -> shortExpirationProvider.getUserId(token))
                .isInstanceOf(TokenExpiredException.class);
    }

    @Test
    @DisplayName("잘못된 형식의 토큰 - TokenInvalidException 발생")
    void parseClaims_Fail_InvalidFormat() {
        // given
        String malformedToken = "not.a.valid.jwt.token.format";

        // when & then
        assertThatThrownBy(() -> jwtProvider.getUserId(malformedToken))
                .isInstanceOf(TokenInvalidException.class);
    }

    @Test
    @DisplayName("Access Token 타입 확인")
    void isAccessToken_Success() {
        // given
        Long userId = 1L;
        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        // when & then
        assertThat(jwtProvider.isAccessToken(accessToken)).isTrue();
        assertThat(jwtProvider.isAccessToken(refreshToken)).isFalse();
    }

    @Test
    @DisplayName("Refresh Token 타입 확인")
    void isRefreshToken_Success() {
        // given
        Long userId = 1L;
        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        // when & then
        assertThat(jwtProvider.isRefreshToken(refreshToken)).isTrue();
        assertThat(jwtProvider.isRefreshToken(accessToken)).isFalse();
    }

    @Test
    @DisplayName("빈 토큰에 대한 검증 실패")
    void validateToken_Fail_EmptyToken() {
        // when & then
        assertThat(jwtProvider.validateToken("")).isFalse();
        assertThat(jwtProvider.validateToken(null)).isFalse();
    }

    @Test
    @DisplayName("다양한 사용자 ID로 토큰 생성 및 검증")
    void createAndValidateTokens_WithDifferentUserIds() {
        // given
        Long[] userIds = {1L, 100L, 99999L};

        for (Long userId : userIds) {
            // when
            String accessToken = jwtProvider.createAccessToken(userId);
            String refreshToken = jwtProvider.createRefreshToken(userId);

            // then
            assertThat(jwtProvider.getUserId(accessToken)).isEqualTo(userId);
            assertThat(jwtProvider.getUserId(refreshToken)).isEqualTo(userId);
            assertThat(jwtProvider.validateToken(accessToken)).isTrue();
            assertThat(jwtProvider.validateToken(refreshToken)).isTrue();
        }
    }
}
