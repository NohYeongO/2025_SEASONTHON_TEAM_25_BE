package com.freedom.saving.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * "기간/금리/적립유형" 옵션의 스냅샷
 * baseList/optionList 매핑 결과를 보존
 * 금리 계산은 도메인 서비스/정책(InterestPolicy)에서 수행
 */
@Getter
@Entity
@Table(
        name = "saving_option_snapshot",
        uniqueConstraints = {
                @UniqueConstraint(name="uk_option", columnNames = {"product_snapshot_id","save_trm","rsrv_type"}),
                @UniqueConstraint(name="uk_option_membership", columnNames = {"id","product_snapshot_id"})
        },
        indexes = @Index(name="idx_option_product", columnList="product_snapshot_id")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavingsOptionSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_snapshot_id", nullable = false)
    private SavingsProductSnapshot productSnapshot;

    @Column(name = "save_trm", nullable = false)   // 개월 수(6/12/24/36..)
    private Integer saveTrm;

    @Enumerated(EnumType.STRING)
    @Column(name = "rsrv_type", nullable = false, length = 1) // 'S'(정액) / 'F'(자유)
    private RsrvType rsrvType;

    @Column(name = "intr_rate_type", length = 8) // 'S' (단리) 등 원문 보존
    private String intrRateType;

    @Column(name = "base_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal baseRate; // 기본금리(%)

    @Column(name = "pref_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal prefRate; // 최고우대금리(%)

    private SavingsOptionSnapshot(Integer saveTrm, RsrvType rsrvType,
                                  String intrRateType, BigDecimal baseRate, BigDecimal prefRate) {
        this.saveTrm = saveTrm;
        this.rsrvType = rsrvType;
        this.intrRateType = intrRateType;
        this.baseRate = baseRate;
        this.prefRate = prefRate;
    }

    public static SavingsOptionSnapshot of(Integer saveTrm, RsrvType rsrvType,
                                           String intrRateType, BigDecimal baseRate, BigDecimal prefRate) {
        if (saveTrm == null || saveTrm <= 0) throw new IllegalArgumentException("기간(saveTrm)은 양수여야 합니다.");
        if (baseRate == null || prefRate == null) throw new IllegalArgumentException("금리는 null일 수 없습니다.");
        return new SavingsOptionSnapshot(saveTrm, rsrvType, intrRateType, baseRate, prefRate);
    }

    /** 부모 연결 전용(setter 금지 대안). */
    public void bindTo(SavingsProductSnapshot parent) {
        if (parent == null) throw new IllegalArgumentException("상품 스냅샷이 필요합니다.");
        this.productSnapshot = parent;
    }

    /** 적금 계산 시 선택되는 적용 금리(기본/우대) 반환 헬퍼 */
    public BigDecimal getAppliedRate(boolean usePreferential) {
        return usePreferential ? prefRate : baseRate;
    }
}
