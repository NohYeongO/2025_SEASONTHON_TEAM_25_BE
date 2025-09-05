package com.freedom.saving.api.subscription;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OpenSubscriptionRequest(
        @NotNull
        Long productSnapshotId,

        // null 허용: 후보가 1개면 서비스에서 자동 선택, 다수면 예외로 유도
        @JsonAlias({"termMoonths"})
        Integer termMonths,

        // null 허용: 자유적립식(F)일 수 있으므로. 값이 있으면 양수여야 함.
        @Positive
        BigDecimal autoDebitAmount,

        // null 허용: "S"/"F" 또는 "FIXED"/"FREE" 등 별칭 허용
        String reserveType
) { }
