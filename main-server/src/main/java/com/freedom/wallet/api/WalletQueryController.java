package com.freedom.wallet.api;

import com.freedom.common.security.CustomUserPrincipal;
import com.freedom.wallet.application.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletQueryController {

    private final WalletService walletService;

    public record BalanceResponse(BigDecimal balance) {}

    @GetMapping("/balance")
    public BalanceResponse getBalance(@AuthenticationPrincipal CustomUserPrincipal principal) {
        var wallet = walletService.getWalletByUserId(principal.getId());
        return new BalanceResponse(wallet.getBalance());
    }
}
