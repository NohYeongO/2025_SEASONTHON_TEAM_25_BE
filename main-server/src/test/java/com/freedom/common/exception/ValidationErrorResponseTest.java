package com.freedom.common.exception;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationErrorResponseTest {

    @Test
    void 검증_에러_응답_생성_테스트() {
        // given
        List<ValidationFieldError> fieldErrors = Arrays.asList(
                ValidationFieldError.of("email", "invalid-email", "Email", "올바른 이메일 형식이어야 합니다."),
                ValidationFieldError.of("password", "123", "Size", "비밀번호는 8자 이상 20자 이하여야 합니다."),
                ValidationFieldError.of("nickname", "", "NotBlank", "닉네임은 필수입니다.")
        );
        
        // when
        ValidationErrorResponse response = ValidationErrorResponse.of(ErrorCode.VALIDATION_ERROR, fieldErrors);
        
        // then
        assertThat(response.getCode()).isEqualTo("VALIDATION001");
        assertThat(response.getMessage()).isEqualTo("입력값 검증에 실패했습니다.");
        assertThat(response.getErrors()).hasSize(3);
        assertThat(response.getTimestamp()).isNotNull();
        
        // 개별 필드 에러 검증
        ValidationFieldError emailError = response.getErrors().get(0);
        assertThat(emailError.getField()).isEqualTo("email");
        assertThat(emailError.getRejectedValue()).isEqualTo("invalid-email");
        assertThat(emailError.getCode()).isEqualTo("Email");
        assertThat(emailError.getMessage()).isEqualTo("올바른 이메일 형식이어야 합니다.");
    }
    
    @Test
    void ValidationFieldError_생성_테스트() {
        // given & when
        ValidationFieldError fieldError = ValidationFieldError.of(
                "email", 
                "test@", 
                "Email", 
                "올바른 이메일 형식이어야 합니다."
        );
        
        // then
        assertThat(fieldError.getField()).isEqualTo("email");
        assertThat(fieldError.getRejectedValue()).isEqualTo("test@");
        assertThat(fieldError.getCode()).isEqualTo("Email");
        assertThat(fieldError.getMessage()).isEqualTo("올바른 이메일 형식이어야 합니다.");
    }
}
