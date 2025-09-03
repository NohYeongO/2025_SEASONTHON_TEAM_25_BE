package com.freedom.wallet.infra;

import com.freedom.wallet.domain.UserWallet;
import com.freedom.wallet.domain.WalletTransaction;
import com.freedom.wallet.domain.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * WalletTransactionRepository JPA 구현체
 */
@Component
@RequiredArgsConstructor
public class WalletTransactionJpaAdapter implements WalletTransactionRepository {

    private final WalletTransactionJpaRepository jpaRepository;

    @Override
    public WalletTransaction save(WalletTransaction transaction) {
        return jpaRepository.save(transaction);
    }

    @Override
    public Optional<WalletTransaction> findByRequestId(String requestId) {
        return jpaRepository.findByRequestId(requestId);
    }

    @Override
    public List<WalletTransaction> findByWalletIdOrderByCreatedAtDesc(Long walletId) {
        return jpaRepository.findByWalletIdOrderByCreatedAtDesc(walletId);
    }

    /**
     * 비관적 락으로 지갑 조회 (동시성 제어용)
     */
    public Optional<UserWallet> findWalletByIdWithLock(Long walletId) {
        return jpaRepository.findWalletByIdWithLock(walletId);
    }
}
