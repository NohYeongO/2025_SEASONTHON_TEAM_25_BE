package com.freedom.auth.domain.service;

import com.freedom.auth.domain.RefreshToken;
import com.freedom.auth.infra.RefreshTokenJpaRepository;
import com.freedom.common.exception.custom.RefreshTokenExpiredException;
import com.freedom.common.exception.custom.RefreshTokenInvalidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenService 단위 테스트")
class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Mock
    private RefreshTokenJpaRepository refreshTokenRepository;

    @Test
    @DisplayName("리프레시 토큰 저장 성공")
    void saveRefreshToken_Success() {
        // given
        Long userId = 1L;
        String token = "refresh_token_12345";
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(14);
        
        given(refreshTokenRepository.save(any(RefreshToken.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when & then
        assertDoesNotThrow(() -> refreshTokenService.saveRefreshToken(userId, token, expiresAt));
        
        then(refreshTokenRepository).should().save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("토큰으로 리프레시 토큰 조회 성공 - 유효한 토큰")
    void findByToken_Success_ValidToken() {
        // given
        String token = "valid_refresh_token";
        LocalDateTime futureTime = LocalDateTime.now().plusDays(7);
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(1L)
                .token(token)
                .expiresAt(futureTime)
                .build();
        
        given(refreshTokenRepository.findByToken(token)).willReturn(Optional.of(refreshToken));

        // when
        RefreshToken result = refreshTokenService.findByToken(token);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(token);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.isExpired()).isFalse();
        
        then(refreshTokenRepository).should().findByToken(token);
    }

    @Test
    @DisplayName("토큰으로 리프레시 토큰 조회 실패 - 존재하지 않는 토큰")
    void findByToken_Fail_TokenNotFound() {
        // given
        String token = "nonexistent_token";
        given(refreshTokenRepository.findByToken(token)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> refreshTokenService.findByToken(token))
                .isInstanceOf(RefreshTokenInvalidException.class)
                .hasMessageContaining("토큰을 찾을 수 없음");
        
        then(refreshTokenRepository).should().findByToken(token);
    }

    @Test
    @DisplayName("토큰으로 리프레시 토큰 조회 실패 - 만료된 토큰")
    void findByToken_Fail_ExpiredToken() {
        // given
        String token = "expired_refresh_token";
        LocalDateTime pastTime = LocalDateTime.now().minusDays(1);
        RefreshToken expiredToken = RefreshToken.builder()
                .userId(1L)
                .token(token)
                .expiresAt(pastTime)
                .build();
        
        given(refreshTokenRepository.findByToken(token)).willReturn(Optional.of(expiredToken));

        // when & then
        assertThatThrownBy(() -> refreshTokenService.findByToken(token))
                .isInstanceOf(RefreshTokenExpiredException.class);
        
        then(refreshTokenRepository).should().findByToken(token);
    }

    @Test
    @DisplayName("리프레시 토큰 삭제 성공")
    void deleteRefreshToken_Success() {
        // given
        String token = "token_to_delete";
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(1L)
                .token(token)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        
        given(refreshTokenRepository.findByToken(token)).willReturn(Optional.of(refreshToken));

        // when & then
        assertDoesNotThrow(() -> refreshTokenService.deleteRefreshToken(token));
        
        then(refreshTokenRepository).should().findByToken(token);
        then(refreshTokenRepository).should().delete(refreshToken);
    }

    @Test
    @DisplayName("리프레시 토큰 삭제 실패 - 존재하지 않는 토큰")
    void deleteRefreshToken_Fail_TokenNotFound() {
        // given
        String token = "nonexistent_token";
        given(refreshTokenRepository.findByToken(token)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> refreshTokenService.deleteRefreshToken(token))
                .isInstanceOf(RefreshTokenInvalidException.class)
                .hasMessageContaining("삭제할 토큰을 찾을 수 없음");
        
        then(refreshTokenRepository).should().findByToken(token);
    }
}
