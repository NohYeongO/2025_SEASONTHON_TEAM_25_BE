package com.freedom.saving.application;

import com.freedom.common.time.TimeProvider;
import com.freedom.saving.domain.policy.TickPolicy;

import java.time.LocalDate;

// 날짜 계산 한 곳에 모아서 API/Command 서비스에서 재사용 용도
public class SavingsDateService {

    private final TimeProvider timeProvider;
    private final TickPolicy tickPolicy;

    public SavingsDateService(TimeProvider timeProvider, TickPolicy tickPolicy) {
        this.timeProvider = timeProvider;
        this.tickPolicy = tickPolicy;
    }

    public LocalDate firstTransferDate(LocalDate joinDate) {
        return tickPolicy.calcFirstTransferDate(joinDate);
    }

    public LocalDate maturityDate(LocalDate joinDate, int termMonths) {
        return tickPolicy.calcMaturityDate(joinDate, termMonths);
    }

    public LocalDate nextTransferDate(LocalDate joinDate, int currentTick) {
        return tickPolicy.calcNextTransferDate(joinDate, currentTick);
    }

    public int currentTick(LocalDate joinDate, int termMonths) {
        LocalDate today = timeProvider.today();
        return tickPolicy.estimateCurrentTick(joinDate, today, termMonths);
    }
}
