package com.freedom.saving.infra.subscription;

import com.freedom.saving.application.port.SavingProductSnapshotPort;
import com.freedom.saving.infra.snapshot.SavingProductOptionSnapshotJpaRepository;
import com.freedom.saving.infra.snapshot.SavingProductSnapshotJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SavingProductSnapshotAdapter implements SavingProductSnapshotPort {

    private final SavingProductSnapshotJpaRepository productRepo;
    private final SavingProductOptionSnapshotJpaRepository optionRepo;

    /** 상품 스냅샷 존재 여부 */
    @Override
    public boolean existsSnapshot(Long productSnapshotId) {
        return productRepo.existsById(productSnapshotId);
    }

    /** 스냅샷 기준 지원 기간(개월) 목록 */
    @Override
    public List<Integer> getSupportedTermMonths(Long productSnapshotId) {
        return optionRepo.findDistinctTerms(productSnapshotId);
    }

    /** 특정 기간에서 지원하는 적립유형 코드 집합("S"/"F" 등) */
    @Override
    public List<String> getSupportedReserveTypes(Long productSnapshotId, int termMonths) {
        return optionRepo.findDistinctReserveTypes(productSnapshotId, termMonths);
    }

    /** (방어용) 기간/유형 조합이 실제 존재하는지 */
    @Override
    public boolean existsOption(Long productSnapshotId, int termMonths, String reserveTypeCode) {
        // 입력 코드 정규화(앞뒤 공백 제거 + 대문자)
        String code = reserveTypeCode == null ? null : reserveTypeCode.trim().toUpperCase();
        return optionRepo.existsByProductSnapshotIdAndSaveTrmMonthsAndRsrvType(productSnapshotId, termMonths, code);
    }

    /** 가입 발생 시 인기 집계 증가 */
    @Override
    @Transactional
    public void incrementSubscriberCount(Long productSnapshotId) {
        productRepo.findById(productSnapshotId).ifPresent(s -> {
            try {
                java.lang.reflect.Field f = s.getClass().getDeclaredField("subscriberCount");
                f.setAccessible(true);
                Long cur = (Long) f.get(s);
                f.set(s, cur == null ? 1L : cur + 1L);
            } catch (Exception ignore) {}
            productRepo.save(s);
        });
    }
}
