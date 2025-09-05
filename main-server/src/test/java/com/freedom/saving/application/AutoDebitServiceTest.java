package com.freedom.saving.application;

import com.freedom.auth.domain.User;
import com.freedom.auth.infra.UserJpaRepository;
import com.freedom.saving.domain.payment.SavingPaymentHistory;
import com.freedom.saving.domain.payment.SavingPaymentHistoryRepository;
import com.freedom.saving.domain.subscription.SavingSubscription;
import com.freedom.saving.domain.subscription.SubscriptionStatus;
import com.freedom.saving.infra.snapshot.SavingSubscriptionJpaRepository;
import com.freedom.wallet.application.SavingTransactionService;
import com.freedom.wallet.domain.WalletTransaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutoDebitServiceTest {

    @InjectMocks // 테스트 대상 클래스. @Mock으로 만든 가짜 객체들을 이 클래스에 주입합니다.
    private AutoDebitService autoDebitService;

    // --- 의존성 Mocking ---
    @Mock
    private UserJpaRepository userRepo;
    @Mock
    private SavingSubscriptionJpaRepository subscriptionRepo;
    @Mock
    private SavingPaymentHistoryRepository paymentRepo;
    @Mock
    private SavingTransactionService savingTxnService;
    @Mock
    private PlatformTransactionManager txManager;
    @Mock
    private TransactionStatus transactionStatus; // 트랜잭션 상태를 나타내는 가짜 객체

    @Test
    @DisplayName("성공: 자동납입일 첫 호출 시, 활성 구독 건을 정상적으로 처리한다")
    void runOncePerDay_success_processesActiveSubscription() {
        // given (주어진 상황)
        Long userId = 1L;
        User mockUser = mock(User.class);
        SavingSubscription mockSubscription = mock(SavingSubscription.class);
        SavingPaymentHistory mockPayment = mock(SavingPaymentHistory.class);
        WalletTransaction mockTransaction = mock(WalletTransaction.class);
        BigDecimal paymentAmount = new BigDecimal("100000");

        // Mock 객체들의 행동 정의
        when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.getLastAutoPaymentDate()).thenReturn(LocalDate.now().minusDays(1)); // 어제 처리됨
        when(subscriptionRepo.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)).thenReturn(List.of(mockSubscription));
        when(paymentRepo.findNextPlannedPayment(any())).thenReturn(Optional.of(mockPayment));
        when(mockPayment.getExpectedAmount()).thenReturn(paymentAmount);
        when(savingTxnService.processSavingAutoDebit(anyLong(), anyString(), eq(paymentAmount), any())).thenReturn(mockTransaction);
        when(txManager.getTransaction(any())).thenReturn(transactionStatus);

        // when (무엇을 할 때)
        autoDebitService.runOncePerDay(userId);

        // then (이런 결과가 나와야 한다)
        // 1. 출금 및 거래 생성 서비스가 호출되었는가?
        verify(savingTxnService, times(1)).processSavingAutoDebit(anyLong(), anyString(), eq(paymentAmount), any());
        // 2. 납입 이력이 'PAID'로 정상 처리되었는가?
        verify(mockPayment, times(1)).markPaid(eq(paymentAmount), any(), isNull());
        // 3. 오늘 날짜로 최종 처리 기록이 업데이트 되었는가?
        verify(mockUser, times(1)).updateLastAutoPaymentDate(LocalDate.now());
        // 4. 트랜잭션이 롤백 없이 모두 커밋되었는가?
        verify(txManager, atLeast(2)).commit(any()); // 최소 2번 이상 커밋 (납입, 날짜 업데이트)
        verify(txManager, never()).rollback(any());
    }

    @Test
    @DisplayName("성공: 오늘 이미 자동납입이 처리되었으면, 추가 동작 없이 즉시 종료된다")
    void runOncePerDay_alreadyProcessed_shouldReturnEarly() {
        // given
        Long userId = 1L;
        User mockUser = mock(User.class);
        when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.getLastAutoPaymentDate()).thenReturn(LocalDate.now()); // 오늘 이미 처리됨

        // when
        autoDebitService.runOncePerDay(userId);

        // then
        // 다른 어떤 의존성도 호출되지 않았음을 검증
        verify(subscriptionRepo, never()).findByUserIdAndStatus(any(), any());
        verify(paymentRepo, never()).findNextPlannedPayment(any());
        verify(savingTxnService, never()).processSavingAutoDebit(any(), any(), any(), any());
    }

    @Test
    @DisplayName("실패: 잔액 부족으로 출금 실패 시, 미납 처리하고 강제 해지는 하지 않는다 (3회 미만)")
    void runOncePerDay_paymentFails_shouldMarkAsMissed() {
        // given
        Long userId = 1L;
        User mockUser = mock(User.class);
        SavingSubscription mockSubscription = mock(SavingSubscription.class);
        SavingPaymentHistory mockPayment = mock(SavingPaymentHistory.class);

        // 출금 서비스가 예외를 던지는 상황을 시뮬레이션
        when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.getLastAutoPaymentDate()).thenReturn(null);
        when(subscriptionRepo.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)).thenReturn(List.of(mockSubscription));
        when(paymentRepo.findNextPlannedPayment(any())).thenReturn(Optional.of(mockPayment));
        when(mockPayment.getExpectedAmount()).thenReturn(new BigDecimal("100000"));
        when(savingTxnService.processSavingAutoDebit(any(), any(), any(), any())).thenThrow(new IllegalArgumentException("잔액 부족"));
        when(txManager.getTransaction(any())).thenReturn(transactionStatus);
        when(paymentRepo.countBySubscriptionIdAndStatus(any(), eq(SavingPaymentHistory.PaymentStatus.MISSED))).thenReturn(1L); // 현재까지 미납 1회

        // when
        autoDebitService.runOncePerDay(userId);

        // then
        // 1. 첫 번째 트랜잭션은 롤백되었는가?
        verify(txManager, times(1)).rollback(any());
        // 2. 납입 이력이 'MISSED'로 처리되었는가?
        verify(mockPayment, times(1)).markMissed();
        // 3. 강제 해지 로직은 호출되지 않았는가?
        verify(mockSubscription, never()).forceCancel();
    }

    @Test
    @DisplayName("실패: 3번째 잔액 부족 발생 시, 미납 처리 후 구독을 강제 해지한다")
    void runOncePerDay_thirdPaymentFails_shouldForceCancelSubscription() {
        // given
        Long userId = 1L;
        User mockUser = mock(User.class);
        SavingSubscription mockSubscription = mock(SavingSubscription.class);
        SavingPaymentHistory mockPayment = mock(SavingPaymentHistory.class);

        when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.getLastAutoPaymentDate()).thenReturn(null);
        when(subscriptionRepo.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)).thenReturn(List.of(mockSubscription));
        when(paymentRepo.findNextPlannedPayment(any())).thenReturn(Optional.of(mockPayment));
        when(mockPayment.getExpectedAmount()).thenReturn(new BigDecimal("100000"));
        when(savingTxnService.processSavingAutoDebit(any(), any(), any(), any())).thenThrow(new IllegalArgumentException("잔액 부족"));
        when(txManager.getTransaction(any())).thenReturn(transactionStatus);
        when(paymentRepo.countBySubscriptionIdAndStatus(any(), eq(SavingPaymentHistory.PaymentStatus.MISSED))).thenReturn(3L); // 이미 미납 2회 누적 -> 이번이 3번째
        when(mockSubscription.getStatus()).thenReturn(SubscriptionStatus.ACTIVE);

        // when
        autoDebitService.runOncePerDay(userId);

        // then
        // 1. 첫 트랜잭션은 롤백되었는가?
        verify(txManager, times(1)).rollback(any());
        // 2. 납입 이력이 'MISSED'로 처리되었는가?
        verify(mockPayment, times(1)).markMissed();
        // 3. 구독 강제 해지 로직이 호출되었는가?
        verify(mockSubscription, times(1)).forceCancel();
        // 4. 변경된 구독 상태가 저장되었는가?
        verify(subscriptionRepo, times(1)).save(mockSubscription);
    }
}
