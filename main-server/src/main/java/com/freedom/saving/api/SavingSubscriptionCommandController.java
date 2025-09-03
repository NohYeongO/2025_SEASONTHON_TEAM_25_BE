package com.freedom.saving.api;

import com.freedom.saving.api.subscription.OpenSubscriptionRequest;
import com.freedom.saving.api.subscription.OpenSubscriptionResponse;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OpenSubscriptionResponse open(
            @AuthenticationPrincipal(expression = "id") Long userId, // 커스텀 Principal의 id만 주입
            @RequestBody @Validated OpenSubscriptionRequest req
    ) {
        var cmd = new OpenSubscriptionCommand(
                userId,
                req.productSnapshotId(),
                req.termMonths(),
                req.reserveType(),      // 선택값. 미전달 시 서비스가 후보 1개면 자동, 2개 이상이면 예외
                req.autoDebitAmount()   // 정액식(S)일 때만 서비스에서 필수 검증
        );
        OpenSubscriptionResult r = service.open(cmd);
        return new OpenSubscriptionResponse(
                r.subscriptionId(),
                r.startDate(),
                r.maturityDate()
        );
    }
}
