package com.freedom.saving.application;

import com.freedom.common.time.TimeProvider;
import com.freedom.saving.domain.payment.SavingPaymentHistoryRepository;
import com.freedom.saving.domain.subscription.SavingSubscription;
import com.freedom.saving.domain.subscription.SubscriptionStatus;
import com.freedom.saving.infra.snapshot.SavingSubscriptionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SavingSubscriptionQueryService {

    private final SavingSubscriptionJpaRepository subscriptionRepo;
    private final SavingPaymentHistoryRepository paymentRepo;
    private final TimeProvider timeProvider;

    public record ActiveDto(Long subscriptionId, String productName, BigDecimal currentAmount,
                            int progressPercentage, String joinDate, String maturityDate, int remainingPayments) {}

    public record CompletedDto(Long subscriptionId, String productName, BigDecimal finalAmount,
                               String joinDate, String maturityDate) {}

    public List<ActiveDto> getActive(Long userId) {
        List<SavingSubscription> subs = subscriptionRepo.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE);
        return subs.stream().map(this::toActive).toList();
    }

    public List<CompletedDto> getCompleted(Long userId) {
        List<SavingSubscription> subs = subscriptionRepo.findByUserIdAndStatusIn(userId,
                List.of(SubscriptionStatus.MATURED, SubscriptionStatus.CANCELLED, SubscriptionStatus.FORCED_CANCELLED));
        return subs.stream().map(this::toCompleted).toList();
    }

    private ActiveDto toActive(SavingSubscription s) {
        BigDecimal target = s.getAutoDebitAmount().getValue().multiply(BigDecimal.valueOf(s.getTerm().getValue()));
        BigDecimal paid = paymentRepo.calculateTotalPaidAmount(s.getId());
        int progress = target.signum() == 0 ? 0 : paid.multiply(BigDecimal.valueOf(100)).divide(target, 0, java.math.RoundingMode.HALF_UP).intValue();
        int remaining = Math.max(0, s.getTerm().getValue() - (int) paymentRepo.countBySubscriptionIdAndStatus(s.getId(), com.freedom.saving.domain.payment.SavingPaymentHistory.PaymentStatus.PAID));
        return new ActiveDto(
                s.getId(),
                "", // 상품명은 스냅샷에서 추가 가능(지금은 필수 아님)
                paid,
                Math.min(100, progress),
                s.getDates().getStartDate().toString(),
                s.getDates().getMaturityDate().toString(),
                remaining
        );
    }

    private CompletedDto toCompleted(SavingSubscription s) {
        BigDecimal finalAmount = paymentRepo.calculateTotalPaidAmount(s.getId());
        return new CompletedDto(
                s.getId(),
                "",
                finalAmount,
                s.getDates().getStartDate().toString(),
                s.getDates().getMaturityDate().toString()
        );
    }
}
