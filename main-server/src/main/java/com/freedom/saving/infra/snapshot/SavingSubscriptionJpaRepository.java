package com.freedom.saving.infra.snapshot;

import com.freedom.saving.domain.subscription.SavingSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingSubscriptionJpaRepository extends JpaRepository<SavingSubscription, Long> {
}
