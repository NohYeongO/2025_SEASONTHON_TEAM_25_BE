package com.freedom.wallet.application;

import com.freedom.wallet.domain.UserWallet;
import com.freedom.wallet.domain.UserWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 지갑 기본 관리 서비스
 * - 지갑 생성 및 기본 조회 기능
 */
@Service
@RequiredArgsConstructor
@Transactional
public class WalletService {

    private final UserWalletRepository walletRepository;

    /**
     * 사용자 지갑 생성
     * @param userId 사용자 ID
     * @return 생성된 지갑
     */
    public UserWallet createWallet(Long userId) {
        if (walletRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("사용자 " + userId + "의 지갑이 이미 존재합니다.");
        }
        
        UserWallet wallet = UserWallet.create(userId);
        return walletRepository.save(wallet);
    }

    /**
     * 사용자 지갑 조회
     * @param userId 사용자 ID
     * @return 사용자 지갑
     */
    @Transactional(readOnly = true)
    public UserWallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 지갑을 찾을 수 없습니다. userId: " + userId));
    }

    /**
     * 잔액 확인
     * @param userId 사용자 ID
     * @param amount 확인할 금액
     * @return 잔액 충분 여부
     */
    @Transactional(readOnly = true)
    public boolean hasEnoughBalance(Long userId, BigDecimal amount) {
        UserWallet wallet = getWalletByUserId(userId);
        return wallet.hasEnoughBalance(amount);
    }
}