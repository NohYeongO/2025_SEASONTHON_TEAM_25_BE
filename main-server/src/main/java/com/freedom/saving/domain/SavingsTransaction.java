package com.freedom.saving.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * "월 단위 납입 기록"
 * (subscription_id, yyyymm) UNIQUE로 멱등성 보장(중복 요청 방지)
 */
@Getter
@Entity
@Table(
        name = "saving_tx",
        uniqueConstraints = @UniqueConstraint(name = "uk_subscription_month", columnNames = {"subscription_id", "yyyymm"}),
        indexes = @Index(name = "idx_product_code", columnList = "fin_co_no, fin_prdt_cd")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavingsTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private SavingsSubscription subscription;

    @Column(name = "yyyymm", nullable = false, length = 6)
    private String yyyymm; // 예: "202509"
    @Column(name = "amount", nullable = false)
    private Integer amount;

    @CreationTimestamp
    @Column(name = "paid_at", nullable = false, updatable = false)
    private Instant paidAt;

    private SavingsTransaction(SavingsSubscription subscription, String yyyymm, Integer amount) {
        this.subscription = subscription;
        this.yyyymm = yyyymm;
        this.amount = amount;
    }

    // 형식/범위 검증 포함한 팩토리
    public static SavingsTransaction of(SavingsSubscription subscription, String yyyymm, Integer amount) {
        if (subscription == null) throw new IllegalArgumentException("subscription 필수");
        if (yyyymm == null || !yyyymm.matches("^\\d{6}$"))
            throw new IllegalArgumentException("yyyymm 형식은 YYYYMM(6자리)이어야 합니다.");
        if (amount == null || amount <= 0) throw new IllegalArgumentException("amount는 양수여야 합니다.");
        return new SavingsTransaction(subscription, yyyymm, amount);
    }
}
