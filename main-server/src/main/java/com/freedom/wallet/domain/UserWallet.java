package com.freedom.wallet.domain;

import com.freedom.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 사용자 지갑 엔티티
 * - 사용자당 1개의 지갑만 보유
 * - 잔액은 원 단위로 관리
 */
@Getter
@Entity
@Table(name = "user_wallet",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_wallet_user_id", columnNames = {"user_id"})
        },
        indexes = {
                @Index(name = "idx_user_wallet_user_id", columnList = "user_id")
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserWallet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "balance", nullable = false, precision = 15, scale = 0)
    private BigDecimal balance; // 잔액 (원 단위)

    @Version
    @Column(name = "version", nullable = false)
    private Long version; // 낙관적 락용 버전

    /**
     * 지갑 생성 팩토리
     * - 사용자 가입 시 초기 지갑 생성
     */
    public static UserWallet create(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        
        UserWallet wallet = new UserWallet();
        wallet.userId = userId;
        wallet.balance = BigDecimal.ZERO;
        wallet.version = 0L; // 초기 버전
        return wallet;
    }

    /**
     * 입금 처리
     * @param amount 입금 금액 (원)
     */
    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("입금 금액은 0보다 커야 합니다.");
        }
        
        this.balance = this.balance.add(amount);
    }

    /**
     * 출금 처리
     * @param amount 출금 금액 (원)
     */
    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("출금 금액은 0보다 커야 합니다.");
        }
        
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("잔액이 부족합니다. 현재 잔액: " + this.balance + "원, 요청 금액: " + amount + "원");
        }
        
        this.balance = this.balance.subtract(amount);
    }

    /**
     * 잔액 확인
     * @param amount 확인할 금액
     * @return 잔액 충분 여부
     */
    public boolean hasEnoughBalance(BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }

}
