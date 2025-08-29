package com.freedom.auth.domain.service;

import com.freedom.auth.domain.User;
import com.freedom.auth.domain.UserRole;
import com.freedom.auth.domain.UserStatus;
import com.freedom.auth.infra.UserJpaRepository;
import com.freedom.common.exception.custom.DuplicateEmailException;
import com.freedom.common.exception.custom.InvalidPasswordException;
import com.freedom.common.exception.custom.UserSuspendedException;
import com.freedom.common.exception.custom.UserWithdrawnException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("ValidateUserService 단위 테스트")
class ValidateUserServiceTest {

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ValidateUserService validateUserService;

    @Test
    @DisplayName("이메일 중복 검증 - 중복되지 않은 경우")
    void validateEmailDuplication_Success() {
        // given
        String email = "test@example.com";
        given(userRepository.existsByEmail(email)).willReturn(false);

        // when & then
        assertDoesNotThrow(() -> validateUserService.validateEmailDuplication(email));
    }

    @Test
    @DisplayName("이메일 중복 검증 - 중복된 경우")
    void validateEmailDuplication_Fail_DuplicateEmail() {
        // given
        String email = "test@example.com";
        given(userRepository.existsByEmail(email)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> validateUserService.validateEmailDuplication(email))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    @DisplayName("비밀번호 검증 - 일치하는 경우")
    void validatePassword_Success() {
        // given
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";
        given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(true);

        // when & then
        assertDoesNotThrow(() -> validateUserService.validatePassword(rawPassword, encodedPassword));
    }

    @Test
    @DisplayName("비밀번호 검증 - 일치하지 않는 경우")
    void validatePassword_Fail_InvalidPassword() {
        // given
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";
        given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> validateUserService.validatePassword(rawPassword, encodedPassword))
                .isInstanceOf(InvalidPasswordException.class);
    }

    @Test
    @DisplayName("사용자 상태 검증 - 활성 사용자")
    void validateUserStatus_Success_ActiveUser() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();

        // when & then
        assertDoesNotThrow(() -> validateUserService.validateUserStatus(user));
    }

    @Test
    @DisplayName("사용자 상태 검증 - 탈퇴한 사용자")
    void validateUserStatus_Fail_WithdrawnUser() {
        // given
        User user = User.builder()
                .email("withdrawn@example.com")
                .password("password")
                .role(UserRole.USER)
                .status(UserStatus.WITHDRAWN)
                .build();

        // when & then
        assertThatThrownBy(() -> validateUserService.validateUserStatus(user))
                .isInstanceOf(UserWithdrawnException.class);
    }

    @Test
    @DisplayName("사용자 상태 검증 - 정지된 사용자")
    void validateUserStatus_Fail_SuspendedUser() {
        // given
        User user = User.builder()
                .email("suspended@example.com")
                .password("password")
                .role(UserRole.USER)
                .status(UserStatus.SUSPENDED)
                .build();

        // when & then
        assertThatThrownBy(() -> validateUserService.validateUserStatus(user))
                .isInstanceOf(UserSuspendedException.class);
    }
}
