package com.freedom.saving.infra.payment;

import com.freedom.saving.domain.payment.SavingPaymentHistory;
import com.freedom.saving.domain.payment.SavingPaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SavingPaymentHistoryJpaAdapter implements SavingPaymentHistoryRepository {

    private final SavingPaymentHistoryJpaRepository jpaRepository;

    @Override
    public SavingPaymentHistory save(SavingPaymentHistory entity) {
        return jpaRepository.save(entity);
    }

    @Override
    public BigDecimal calculateTotalPaidAmount(Long subscriptionId) {
        return jpaRepository.calculateTotalPaidAmount(subscriptionId);
    }

    @Override
    public long countBySubscriptionIdAndStatus(Long subscriptionId, SavingPaymentHistory.PaymentStatus status) {
        return jpaRepository.countBySubscriptionIdAndStatus(subscriptionId, status);
    }

    @Override
    public Optional<SavingPaymentHistory> findNextPlannedPayment(Long subscriptionId) {
        return jpaRepository.findNextPlannedPayment(subscriptionId);
    }

    @Override
    public Optional<SavingPaymentHistory> findBySubscriptionIdAndCycleNo(Long subscriptionId, Integer cycleNo) {
        return jpaRepository.findBySubscriptionIdAndCycleNo(subscriptionId, cycleNo);
    }
}
