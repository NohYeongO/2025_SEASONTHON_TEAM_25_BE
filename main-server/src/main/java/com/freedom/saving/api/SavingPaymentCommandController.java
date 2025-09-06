package com.freedom.saving.api;

import com.freedom.common.security.CustomUserPrincipal;
import com.freedom.saving.application.SavingPaymentCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/savings/subscriptions")
@Validated
@RequiredArgsConstructor
public class SavingPaymentCommandController {

    private final SavingPaymentCommandService commandService;

    @PostMapping("/{subscriptionId}/deposit")
    public void deposit(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long subscriptionId
    ) {
        // 가입 시 설정된 자동이체 금액(예정 금액)으로 납입 처리
        commandService.depositNext(principal.getId(), subscriptionId, null);
    }
}
