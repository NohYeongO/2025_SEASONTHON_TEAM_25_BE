package com.freedom.auth.domain.service;

import com.freedom.auth.domain.User;
import com.freedom.auth.domain.UserRole;
import com.freedom.auth.domain.UserStatus;
import com.freedom.auth.infra.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignUpUserService {
    
    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public User signUp(String email, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        
        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();
        
        return userRepository.save(user);
    }
}