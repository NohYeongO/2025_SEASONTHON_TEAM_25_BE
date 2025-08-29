package com.freedom.auth.domain.service;

import com.freedom.auth.domain.User;
import com.freedom.auth.domain.UserRole;
import com.freedom.auth.domain.UserStatus;
import com.freedom.auth.infra.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("SignUpUserService 단위 테스트")
class SignUpUserServiceTest {

    @InjectMocks
    private SignUpUserService signUpUserService;

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 성공 - 사용자 정보가 올바르게 저장된다")
    void signUp_Success() {
        // given
        String email = "test@example.com";
        String rawPassword = "testpass123!";
        String encodedPassword = "encoded_password_hash";

        given(passwordEncoder.encode(rawPassword)).willReturn(encodedPassword);
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        User result = signUpUserService.signUp(email, rawPassword);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getPassword()).isEqualTo(encodedPassword);
        assertThat(result.getRole()).isEqualTo(UserRole.USER);
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("회원가입 시 비밀번호가 암호화된다")
    void signUp_PasswordEncoded() {
        // given
        String rawPassword = "testpass123!";
        String encodedPassword = "encoded_password_hash";
        
        given(passwordEncoder.encode(rawPassword)).willReturn(encodedPassword);
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        User result = signUpUserService.signUp("test@example.com", rawPassword);

        // then
        assertThat(result.getPassword()).isEqualTo(encodedPassword);
        assertThat(result.getPassword()).isNotEqualTo(rawPassword);
        
        then(passwordEncoder).should().encode(rawPassword);
    }
}
