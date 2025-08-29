package com.freedom.common.security;

import com.freedom.auth.domain.User;
import com.freedom.auth.domain.service.FindUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final FindUserService findUserService;
    
    /**
     * JWT에서 추출한 사용자 ID를 기반으로 UserDetails 로드
     * 
     * @param userId 사용자 ID (JWT subject에서 추출됨)
     * @return UserDetails 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        try {
            Long id = Long.valueOf(userId);
            User user = findUserService.findById(id);
            
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getId().toString())
                    .password(user.getPassword())
                    .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority(user.getRole().getAuthority())
                    ))
                    .accountExpired(false)
                    .accountLocked(user.isSuspended())
                    .credentialsExpired(false)
                    .disabled(user.isWithdrawn())
                    .build();
                    
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid user ID format: " + userId);
        }
    }
}
