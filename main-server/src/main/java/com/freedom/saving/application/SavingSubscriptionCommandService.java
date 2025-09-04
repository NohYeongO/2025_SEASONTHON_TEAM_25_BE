package com.freedom.saving.application;

import com.freedom.saving.domain.subscription.SavingSubscription;
import com.freedom.saving.domain.subscription.SubscriptionStatus;
import com.freedom.saving.infra.snapshot.SavingSubscriptionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SavingSubscriptionCommandService {

    private final SavingSubscriptionJpaRepository subscriptionRepo;

    @Transactional
    public void cancelByUser(Long userId, Long subscriptionId) {
        SavingSubscription sub = subscriptionRepo.findByIdAndUserId(subscriptionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("구독을 찾을 수 없거나 권한이 없습니다."));
        if (sub.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new IllegalStateException("진행중인 구독만 해제할 수 있습니다.");
        }
        sub.cancelByUser();
        subscriptionRepo.save(sub);
    }
}
