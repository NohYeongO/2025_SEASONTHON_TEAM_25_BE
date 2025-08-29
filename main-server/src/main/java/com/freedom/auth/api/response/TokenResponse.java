package com.freedom.auth.api.response;

import com.freedom.auth.application.dto.TokenDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {
    
    private final String accessToken;
    private final String refreshToken;
    private final String tokenType;
    private final Long expiresIn;
    
    public static TokenResponse from(TokenDto dto) {
        return TokenResponse.builder()
                .accessToken(dto.getAccessToken())
                .refreshToken(dto.getRefreshToken())
                .tokenType(dto.getTokenType())
                .expiresIn(dto.getExpiresIn())
                .build();
    }
}
