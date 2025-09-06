package com.freedom.saving.application.signup;

import com.freedom.common.exception.custom.SavingExceptions;

import java.math.BigDecimal;

/**
 * 가입 요청 입력 모델(애플리케이션 계층)
 * - 기간(termMonths), 적립유형(reserveType)은 null 허용: 자동 선택 규칙 적용
 */
public class OpenSubscriptionCommand {

    private final Long userId;
    private final Long productSnapshotId;
    private final Integer termMonths;          // null → 자동 선택 규칙
    private final String reserveType;          // null → 자동 선택 규칙. "S"/"F" 권장
    private final BigDecimal autoDebitAmount;  // 정액식(S)일 때 필수(>0), 자유식(F)일 땐 정책에 따라 선택

    public OpenSubscriptionCommand(Long userId,
                                   Long productSnapshotId,
                                   Integer termMonths,
                                   String reserveType,
                                   BigDecimal autoDebitAmount) {
        if (userId == null || userId.longValue() <= 0L) {
            throw new SavingExceptions.SavingPaymentInvalidParamsException("userId는 필수입니다.");
        }
        if (productSnapshotId == null || productSnapshotId.longValue() <= 0L) {
            throw new SavingExceptions.SavingPaymentInvalidParamsException("productSnapshotId는 필수입니다.");
        }
        this.userId = userId;
        this.productSnapshotId = productSnapshotId;
        this.termMonths = termMonths;      // 선택값
        this.reserveType = reserveType;    // 선택값
        this.autoDebitAmount = autoDebitAmount; // 정책에 따라 필수/선택
    }

    public Long getUserId() { return userId; }
    public Long getProductSnapshotId() { return productSnapshotId; }
    public Integer getTermMonths() { return termMonths; }
    public String getReserveType() { return reserveType; }
    public BigDecimal getAutoDebitAmount() { return autoDebitAmount; }
}
