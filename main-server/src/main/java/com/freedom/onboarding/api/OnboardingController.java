package com.freedom.onboarding.api;

import com.freedom.common.security.CustomUserPrincipal;
import com.freedom.onboarding.api.dto.CharacterCreateRequest;
import com.freedom.onboarding.application.OnboardingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;

    @PostMapping("/character")
    public ResponseEntity<Void> createCharacter(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody CharacterCreateRequest request) {

        onboardingService.createCharacter(principal.getId(), request.getCharacterName());
        return ResponseEntity.noContent().build();
    }
}
