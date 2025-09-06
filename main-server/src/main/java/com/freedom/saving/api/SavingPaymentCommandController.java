package com.freedom.saving.api;

import com.freedom.common.security.CustomUserPrincipal;
import com.freedom.saving.application.SavingPaymentCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> deposit(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long subscriptionId
    ) {
        // 가입 시 지정한 자동이체 금액(PLANNED expectedAmount) 사용
        commandService.depositNext(principal.getId(), subscriptionId, null);
        return ResponseEntity.ok(com.freedom.common.exception.SuccessResponse.ok("적금 납입이 완료되었습니다."));
    }
}
