package com.freedom.saving.api;

import com.freedom.common.security.CustomUserPrincipal;
import com.freedom.saving.api.subscription.OpenSubscriptionRequest;
import com.freedom.saving.api.subscription.OpenSubscriptionResponse;
import com.freedom.saving.application.SavingSubscriptionCommandService;
import com.freedom.saving.application.signup.OpenSubscriptionCommand;
import com.freedom.saving.application.signup.OpenSubscriptionResult;
import com.freedom.saving.application.signup.SavingSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/savings/subscriptions")
@Validated
@RequiredArgsConstructor
public class SavingSubscriptionCommandController {

    private final SavingSubscriptionService service;
    private final SavingSubscriptionCommandService commandService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OpenSubscriptionResponse open(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Validated OpenSubscriptionRequest req
    ) {
        var cmd = new OpenSubscriptionCommand(
                principal.getId(),
                req.productSnapshotId(),
                req.termMonths(),
                req.reserveType(),
                req.autoDebitAmount()
        );
        OpenSubscriptionResult r = service.open(cmd);
        return new OpenSubscriptionResponse(
                r.subscriptionId(),
                r.startDate(),
                r.maturityDate()
        );
    }

    @DeleteMapping("/{subscriptionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long subscriptionId
    ) {
        commandService.cancelByUser(principal.getId(), subscriptionId);
    }
}
