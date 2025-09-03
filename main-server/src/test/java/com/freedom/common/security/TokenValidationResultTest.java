package com.freedom.common.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenValidationResultTest {

    @Test
    @DisplayName("유효한 토큰 결과를 생성한다")
    void createValidResult() {
        // when
        TokenValidationResult result = TokenValidationResult.valid();
        
        // then
        assertThat(result.isValid()).isTrue();
        assertThat(result.isExpired()).isFalse();
        assertThat(result.isInvalid()).isFalse();
        assertThat(result.isWrongType()).isFalse();
        assertThat(result.getStatus()).isEqualTo(TokenValidationResult.Status.VALID);
    }

    @Test
    @DisplayName("만료된 토큰 결과를 생성한다")
    void createExpiredResult() {
        // when
        TokenValidationResult result = TokenValidationResult.expired();
        
        // then
        assertThat(result.isValid()).isFalse();
        assertThat(result.isExpired()).isTrue();
        assertThat(result.isInvalid()).isFalse();
        assertThat(result.isWrongType()).isFalse();
        assertThat(result.getStatus()).isEqualTo(TokenValidationResult.Status.EXPIRED);
        assertThat(result.getMessage()).isEqualTo("만료된 토큰");
    }

    @Test
    @DisplayName("유효하지 않은 토큰 결과를 생성한다")
    void createInvalidResult() {
        // given
        String errorMessage = "서명 오류";
        
        // when
        TokenValidationResult result = TokenValidationResult.invalid(errorMessage);
        
        // then
        assertThat(result.isValid()).isFalse();
        assertThat(result.isExpired()).isFalse();
        assertThat(result.isInvalid()).isTrue();
        assertThat(result.isWrongType()).isFalse();
        assertThat(result.getStatus()).isEqualTo(TokenValidationResult.Status.INVALID);
        assertThat(result.getMessage()).isEqualTo("유효하지 않은 토큰: " + errorMessage);
    }

    @Test
    @DisplayName("잘못된 토큰 타입 결과를 생성한다")
    void createWrongTypeResult() {
        // when
        TokenValidationResult result = TokenValidationResult.wrongType();
        
        // then
        assertThat(result.isValid()).isFalse();
        assertThat(result.isExpired()).isFalse();
        assertThat(result.isInvalid()).isFalse();
        assertThat(result.isWrongType()).isTrue();
        assertThat(result.getStatus()).isEqualTo(TokenValidationResult.Status.WRONG_TYPE);
    }
}
