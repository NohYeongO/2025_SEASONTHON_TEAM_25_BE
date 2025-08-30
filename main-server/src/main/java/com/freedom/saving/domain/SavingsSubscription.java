package com.freedom.saving.domain;

import com.freedom.auth.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;


/**
 * 한 사용자에 대한 "가상 적금 계약"의 상태와 규칙
 * 생성 시 유효성 검증으로 불변성 보장(세터 없음).
 * 옵션이 반드시 해당 상품에 속하는지는 DB의 복합 FK(DDL)로 2차 방어.
 */

@Getter
@Entity
@Table(
        name = "saving_subscription",
        indexes = {
                @Index(name="idx_sub_user", columnList="user_id"),
                @Index(name="idx_sub_product", columnList="product_snapshot_id"),
                @Index(name="idx_sub_option", columnList="option_snapshot_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavingsSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 실제 사용자 FK.
     * - LAZY: 필요할 때만 로딩 → 성능 이점
     * - RESTRICT: 사용자 삭제 시 구독 기록 보호(아카이브 관점)
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_sub_user") // DDL에서도 동일 이름 사용 권장
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_snapshot_id", nullable = false)
    private SavingsProductSnapshot productSnapshot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "option_snapshot_id", nullable = false)
    private SavingsOptionSnapshot optionSnapshot;

    @Column(name = "term_months", nullable = false)
    private Integer termMonths;

    @Column(name = "monthly_deposit", nullable = false)
    private Integer monthlyDeposit;

    @Column(name = "auto_transfer_day", nullable = false)
    private Integer autoTransferDay; // 1~28

    @Enumerated(EnumType.STRING)
    @Column(name = "rate_type", nullable = false, length = 5)
    private RateType rateType; // BASE / PREF

    @Column(name = "months_paid", nullable = false)
    private Integer monthsPaid;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @CreationTimestamp @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp @Column(nullable = false)
    private Instant updatedAt;

    // 세터 없이 생성 시점 검증으로 일관성 확보
    public static SavingsSubscription create(User user,
                                             SavingsProductSnapshot product,
                                             SavingsOptionSnapshot option,
                                             Integer monthlyDeposit,
                                             Integer autoTransferDay,
                                             LocalDate startDate,
                                             RateType rateType) {
        if (user == null || user.getId() == null) throw new IllegalArgumentException("유효한 사용자 필요");
        if (product == null || option == null) throw new IllegalArgumentException("상품/옵션 필수");
        if (!option.getProductSnapshot().getId().equals(product.getId()))
            throw new IllegalArgumentException("옵션이 해당 상품에 속하지 않습니다.");
        if (monthlyDeposit == null || monthlyDeposit <= 0) throw new IllegalArgumentException("월 납입액은 양수");
        if (autoTransferDay == null || autoTransferDay < 1 || autoTransferDay > 28)
            throw new IllegalArgumentException("자동이체일은 1~28");
        if (startDate == null) throw new IllegalArgumentException("startDate 필수");
        if (rateType == null) throw new IllegalArgumentException("rateType 필수");

        var sub = new SavingsSubscription();
        sub.user = user;                             // FK로 연결
        sub.productSnapshot = product;
        sub.optionSnapshot = option;
        sub.termMonths = option.getSaveTrm();
        sub.monthlyDeposit = monthlyDeposit;
        sub.autoTransferDay = autoTransferDay;
        sub.startDate = startDate;
        sub.rateType = rateType;
        sub.monthsPaid = 0;
        return sub;
    }

    public boolean isMatured() { return monthsPaid >= termMonths; }
    public void increasePaidOnce() {
        if (isMatured()) throw new IllegalStateException("이미 만기된 구독입니다.");
        this.monthsPaid += 1;
    }

    public java.math.BigDecimal getAppliedRate() {
        return optionSnapshot.getAppliedRate(rateType == RateType.PREF);
    }
}
