package com.freedom.saving.application.port;

import java.util.List;

/**
 * 스냅샷/옵션 조회 전용 포트(Out Port)
 * - infra에서 JPA를 이용해 구현
 * - 애플리케이션 서비스는 본 추상화를 통해서만 스냅샷/옵션을 조회한다
 */
public interface SavingProductSnapshotPort {

    /** 상품 스냅샷 존재 여부 */
    boolean existsSnapshot(Long productSnapshotId);

    /** 스냅샷 기준 지원 기간(개월) 목록 */
    List<Integer> getSupportedTermMonths(Long productSnapshotId);

    /** 특정 기간에서 지원하는 적립유형 코드 집합("S"/"F" 등) */
    List<String> getSupportedReserveTypes(Long productSnapshotId, int termMonths);

    /** (방어용) 기간/유형 조합이 실제 존재하는지 */
    boolean existsOption(Long productSnapshotId, int termMonths, String reserveTypeCode);

    /** 가입 발생 시 인기 집계 증가 */
    void incrementSubscriberCount(Long productSnapshotId);
}
