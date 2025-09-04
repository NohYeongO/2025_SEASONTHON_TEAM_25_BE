package com.freedom.saving.application;

import com.freedom.common.exception.custom.SavingExceptions;
import com.freedom.saving.domain.payment.SavingPaymentHistory;
import com.freedom.saving.domain.payment.SavingPaymentHistoryRepository;
import com.freedom.saving.domain.subscription.SavingSubscription;
import com.freedom.saving.domain.subscription.SubscriptionStatus;
import com.freedom.saving.infra.snapshot.SavingSubscriptionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SavingPaymentCommandService {

    private final SavingSubscriptionJpaRepository subscriptionRepo;
    private final SavingPaymentHistoryRepository paymentRepo;

    /**
     * 다음 예정 회차(PLANNED)에 대해 납입 처리
     * amount가 null이면 expectedAmount로 처리
     */
    @Transactional
    public void depositNext(Long userId, Long subscriptionId, BigDecimal amount) {
        SavingSubscription sub = subscriptionRepo.findByIdAndUserId(subscriptionId, userId)
                .orElseThrow(SavingExceptions.SavingSubscriptionNotFoundException::new);
        if (sub.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new SavingExceptions.SavingSubscriptionInvalidStateException(sub.getStatus().name());
        }

        SavingPaymentHistory planned = paymentRepo.findNextPlannedPayment(subscriptionId)
                .orElseThrow(SavingExceptions.SavingNoNextPlannedPaymentException::new);

        BigDecimal payAmount = amount != null ? amount : planned.getExpectedAmount();
        if (payAmount == null || payAmount.signum() <= 0) {
            throw new SavingExceptions.SavingInvalidPaymentAmountException();
        }

        planned.markPaid(payAmount, null, null);
        paymentRepo.save(planned);
    }
}
