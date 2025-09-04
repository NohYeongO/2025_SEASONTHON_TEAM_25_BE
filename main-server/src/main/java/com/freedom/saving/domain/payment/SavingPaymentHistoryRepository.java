package com.freedom.saving.domain.payment;

import java.math.BigDecimal;
import java.util.Optional;

public interface SavingPaymentHistoryRepository {

    SavingPaymentHistory save(SavingPaymentHistory entity);

    BigDecimal calculateTotalPaidAmount(Long subscriptionId);

    long countBySubscriptionIdAndStatus(Long subscriptionId, SavingPaymentHistory.PaymentStatus status);

    Optional<SavingPaymentHistory> findNextPlannedPayment(Long subscriptionId);

    Optional<SavingPaymentHistory> findBySubscriptionIdAndCycleNo(Long subscriptionId, Integer cycleNo);
}
