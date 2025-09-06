package com.freedom.saving.api;

import com.freedom.common.security.CustomUserPrincipal;
import com.freedom.saving.application.SavingPaymentCommandService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/savings/subscriptions")
@Validated
@RequiredArgsConstructor
public class SavingPaymentCommandController {

    private final SavingPaymentCommandService commandService;

    public record DepositRequest(@Positive BigDecimal amount) {}

    @PostMapping("/{subscriptionId}/deposit")
    public void deposit(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long subscriptionId,
            @RequestBody(required = false) DepositRequest request
    ) {
        BigDecimal amount = request != null ? request.amount() : null;
        commandService.depositNext(principal.getId(), subscriptionId, amount);
    }
}
