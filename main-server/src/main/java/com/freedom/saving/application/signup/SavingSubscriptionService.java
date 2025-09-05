package com.freedom.saving.application.signup;

import com.freedom.common.time.TimeProvider;
import com.freedom.saving.application.port.SavingProductSnapshotPort;
import com.freedom.saving.application.port.SavingSubscriptionPort;
import com.freedom.saving.application.signup.exception.*;
import com.freedom.saving.domain.policy.TickPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
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
    private final TickPolicy tickPolicy;                    // 1일 = 1개월 정책

    private static final String RESERVE_S = "S"; // 정액적립식
    private static final String RESERVE_F = "F"; // 자유적립식

    private LocalDate serviceToday(ZonedDateTime now) {
        // "현실 하루 = 서비스 한 달", '오늘'을 서비스 기준일로 그대로 쓴다고 가정
        return now.toLocalDate();
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
        LocalDate maturityServiceDate = tickPolicy.calcMaturityDate(startServiceDate, chosenTerm);

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
        // 인기 집계 증가
        snapshotPort.incrementSubscriberCount(cmd.getProductSnapshotId());
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
        if (requestedTerm != null) {
            if (!supported.contains(requestedTerm)) {
                throw new ProductTermNotSupportedException(requestedTerm);
            }
            return requestedTerm;
        }
        if (supported.isEmpty()) {
            throw new MissingTermSelectionException(supported);
        }
        if (supported.size() == 1) {
            return supported.get(0);
        }
        throw new MissingTermSelectionException(supported);
    }

    private String normalizeReserveType(String reserveType) {
        return reserveType == null ? null : reserveType.trim().toUpperCase();
    }

    private String chooseReserveType(Long snapshotId, int termMonths, String requested) {
        List<String> supported = snapshotPort.getSupportedReserveTypes(snapshotId, termMonths);
        if (requested != null) {
            if (!supported.contains(requested)) {
                throw new ReserveTypeNotSupportedException(termMonths, requested);
            }
            return requested;
        }
        if (supported.isEmpty()) {
            throw new MissingReserveTypeSelectionException(termMonths, supported);
        }
        if (supported.size() == 1) {
            return supported.get(0);
        }
        throw new MissingReserveTypeSelectionException(termMonths, supported);
    }

    private void validateAutoDebitAmountForFixed(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidAutoDebitAmountForFixedException(amount);
        }
    }
}
