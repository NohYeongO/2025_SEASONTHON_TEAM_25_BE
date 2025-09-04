package com.freedom.wallet.infra;

import com.freedom.wallet.domain.UserWallet;
import com.freedom.wallet.domain.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

/**
 * 지갑 거래 이력 JPA Repository
 */
@Repository
public interface WalletTransactionJpaRepository extends JpaRepository<WalletTransaction, Long> {

    /**
     * 요청 ID로 거래 이력 조회 (멱등성 확인용)
     */
    Optional<WalletTransaction> findByRequestId(String requestId);

    /**
     * 지갑 ID로 거래 이력 목록 조회 (최신순)
     */
    List<WalletTransaction> findByWallet_IdOrderByCreatedAtDesc(Long walletId);

    /**
     * 비관적 락으로 지갑 조회 (동시성 제어용)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM UserWallet w WHERE w.id = :walletId")
    Optional<UserWallet> findWalletByIdWithLock(@Param("walletId") Long walletId);
}
