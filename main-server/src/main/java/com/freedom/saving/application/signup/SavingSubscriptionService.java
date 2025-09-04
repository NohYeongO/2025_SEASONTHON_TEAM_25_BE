package com.freedom.saving.application.signup;

import com.freedom.common.time.TimeProvider;
import com.freedom.saving.application.port.SavingProductSnapshotPort;
import com.freedom.saving.application.port.SavingSubscriptionPort;
import com.freedom.saving.application.signup.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 적금 가입 유스케이스
 *
 * 설계 메모
 * - SRP: 도메인 규칙(기간/유형 검증)만 담당. 저장/조회는 포트로 위임
 * - 수집 단계에서 fin_prdt_cd 기반으로 옵션-스냅샷이 이미 연결됨.
 *   가입 시점엔 productSnapshotId 하나만 알면 충분.
 */
@Service
@RequiredArgsConstructor
public class SavingSubscriptionService {

    private final SavingProductSnapshotPort snapshotPort;
    private final SavingSubscriptionPort subscriptionPort;
    private final TimeProvider timeProvider;                // 현재 시각

    private static final String RESERVE_S = "S"; // 정액적립식
    private static final String RESERVE_F = "F"; // 자유적립식

    private LocalDate serviceToday(ZonedDateTime now) {
        // "현실 하루 = 서비스 한 달", '오늘'을 서비스 기준일로 그대로 쓴다고 가정
        return now.toLocalDate();
    }
    private LocalDate plusServiceMonths(LocalDate serviceStart, int termMonths) {
        return serviceStart.plusMonths(termMonths);
    }

    @Transactional
    public OpenSubscriptionResult open(OpenSubscriptionCommand cmd) {
        // 1) 스냅샷 존재
        if (!snapshotPort.existsSnapshot(cmd.getProductSnapshotId())) {
            throw new ProductSnapshotNotFoundException(cmd.getProductSnapshotId());
        }

        // 2) 기간 선택 (save_trm 기준)
        int chosenTerm = chooseTerm(cmd.getProductSnapshotId(), cmd.getTermMonths());

        // 3) 적립유형 선택 (기간별)
        String chosenReserve = chooseReserveType(
                cmd.getProductSnapshotId(),
                chosenTerm,
                normalizeReserveType(cmd.getReserveType())
        );

        // 4) 정액식이면 금액 필수
        if (RESERVE_S.equals(chosenReserve)) {
            validateAutoDebitAmountForFixed(cmd.getAutoDebitAmount());
        }

        // 5) 서비스 달력 날짜 계산
        ZonedDateTime now = timeProvider.now();
        LocalDate startServiceDate = serviceToday(now);
        LocalDate maturityServiceDate = plusServiceMonths(startServiceDate, chosenTerm);

        // 6) 저장 (서비스 일자 전달)
        Long subscriptionId = subscriptionPort.open(
                cmd.getUserId(),
                cmd.getProductSnapshotId(),
                chosenTerm,
                chosenReserve,
                cmd.getAutoDebitAmount(),
                startServiceDate,
                maturityServiceDate
        );
        return new OpenSubscriptionResult(subscriptionId, startServiceDate, maturityServiceDate);
    }

    /**
     * 기간 선택 규칙:
     * - 요청 termMonths 가 있으면: 후보 목록에 포함되어야 함
     * - 요청 termMonths 가 없으면:
     *    - 후보가 1개면 자동 선택
     *    - 후보가 2개 이상이면 MissingTermSelectionException(명시 요구)
     */
    private int chooseTerm(Long snapshotId, Integer requestedTerm) {
        List<Integer> supported = snapshotPort.getSupportedTermMonths(snapshotId);
        if (supported == null) supported = new ArrayList<Integer>();
        if (supported.isEmpty()) {
            throw new IllegalStateException("save_trm 후보가 없습니다. snapshotId=" + snapshotId);
        }

        if (requestedTerm != null) {
            boolean exists = false;
            for (int i = 0; i < supported.size(); i++) {
                Integer v = supported.get(i);
                if (v != null && v.intValue() == requestedTerm.intValue()) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                throw new ProductTermNotSupportedException(requestedTerm.intValue());
            }
            return requestedTerm.intValue();
        }

        // 미전달 케이스
        if (supported.size() == 1) {
            Integer only = supported.get(0);
            return only != null ? only.intValue() : 0;
        }
        // 후보가 2개 이상이면 명시 요구(12/24/36 등)
        throw new MissingTermSelectionException(supported);
    }

    /**
     * 적립유형 선택 규칙(기간별):
     * - 요청 reserveType 미전달:
     *    - 후보 1개면 자동 선택
     *    - 후보 2개 이상이면 MissingReserveTypeSelectionException
     * - 요청 전달:
     *    - 후보에 없으면 ReserveTypeNotSupportedException
     */
    private String chooseReserveType(Long snapshotId, int termMonths, String requestedReserve) {
        List<String> supported = snapshotPort.getSupportedReserveTypes(snapshotId, termMonths);
        if (supported == null) supported = new ArrayList<String>();

        if (requestedReserve == null || requestedReserve.trim().isEmpty()) {
            if (supported.size() == 1) {
                return supported.get(0);
            }
            throw new MissingReserveTypeSelectionException(termMonths, supported);
        }

        String value = requestedReserve.toUpperCase();
        boolean exists = false;
        for (int i = 0; i < supported.size(); i++) {
            String s = supported.get(i);
            if (s != null && s.toUpperCase().equals(value)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            throw new ReserveTypeNotSupportedException(termMonths, requestedReserve);
        }
        return value;
    }

    /**
     * 사용자가 "정액/자유" 같은 별칭을 보낼 수 있으니 표준코드로 정규화.
     * - FIXED/REGULAR → S
     * - FLEX/FREE     → F
     * - 그 외는 원문 유지(후보 검증에서 실패 유도)
     */
    private String normalizeReserveType(String reserveType) {
        if (reserveType == null) return null;
        String v = reserveType.trim().toUpperCase();
        if ("FIXED".equals(v) || "REGULAR".equals(v)) return RESERVE_S;
        if ("FLEX".equals(v) || "FREE".equals(v)) return RESERVE_F;
        if (RESERVE_S.equals(v) || RESERVE_F.equals(v)) return v;
        return v;
    }

    /** 정액식(S) 검증: 금액 null 금지 + 0 초과 */
    private void validateAutoDebitAmountForFixed(BigDecimal amount) {
        if (amount == null) throw new InvalidAutoDebitAmountForFixedException(null);
        if (amount.signum() <= 0) throw new InvalidAutoDebitAmountForFixedException(amount);
    }
}
