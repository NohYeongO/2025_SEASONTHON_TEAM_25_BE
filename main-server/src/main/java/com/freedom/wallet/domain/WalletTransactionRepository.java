package com.freedom.wallet.domain;

import java.util.List;
import java.util.Optional;

/**
 * 지갑 거래 이력 도메인 레벨 추상화
 * infra(JPA) 구현체는 wallet/infra 하위에 별도 작성
 */
public interface WalletTransactionRepository {

    /**
     * 거래 이력 저장
     */
    WalletTransaction save(WalletTransaction transaction);

    /**
     * 요청 ID로 거래 이력 조회 (멱등성 확인용)
     */
    Optional<WalletTransaction> findByRequestId(String requestId);

    /**
     * 지갑 ID로 거래 이력 목록 조회 (최신순)
     */
    List<WalletTransaction> findByWalletIdOrderByCreatedAtDesc(Long walletId);
}
