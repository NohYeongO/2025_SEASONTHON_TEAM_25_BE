package com.freedom.auth.api.response;

import com.freedom.auth.application.dto.TokenDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    
    private final String accessToken;
    private final String refreshToken;
    private final String tokenType;
    private final Long expiresIn;
    private final UserInfo user;
    
    public static LoginResponse from(TokenDto dto) {
        return LoginResponse.builder()
                .accessToken(dto.getAccessToken())
                .refreshToken(dto.getRefreshToken())
                .tokenType(dto.getTokenType())
                .expiresIn(dto.getExpiresIn())
                .user(UserInfo.from(dto.getUser()))
                .build();
    }
}
