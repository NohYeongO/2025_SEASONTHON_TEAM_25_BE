package com.freedom.saving.application;

import com.freedom.common.exception.custom.SavingExceptions;
import com.freedom.common.time.TimeProvider;
import com.freedom.saving.domain.SavingProductOptionSnapshot;
import com.freedom.saving.domain.payment.SavingPaymentHistory;
import com.freedom.saving.domain.payment.SavingPaymentHistoryRepository;
import com.freedom.saving.domain.subscription.ServiceDates;
import com.freedom.saving.domain.subscription.SavingSubscription;
import com.freedom.saving.domain.subscription.SubscriptionStatus;
import com.freedom.saving.domain.subscription.TermMonths;
import com.freedom.saving.infra.snapshot.SavingProductOptionSnapshotJpaRepository;
import com.freedom.saving.infra.snapshot.SavingProductSnapshotJpaRepository;
import com.freedom.saving.infra.snapshot.SavingSubscriptionJpaRepository;
import com.freedom.wallet.application.SavingTransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaturitySettlementServiceTest {

    @InjectMocks
    private MaturitySettlementService maturitySettlementService;

    @Mock
    private SavingSubscriptionJpaRepository subscriptionRepo;
    @Mock
    private SavingPaymentHistoryRepository paymentRepo;
    @Mock
    private SavingProductOptionSnapshotJpaRepository optionRepo;
    @Mock
    private SavingProductSnapshotJpaRepository productSnapshotRepo;
    @Mock
    private SavingTransactionService savingTxnService;
    @Mock
    private TimeProvider timeProvider;

    private static final Long USER_ID = 1L;
    private static final Long SUBSCRIPTION_ID = 100L;
    private static final Long PRODUCT_SNAPSHOT_ID = 200L;


    @Test
    @DisplayName("성공: 만기일이 된 적금을 정상적으로 정산하고 상태를 MATURED로 변경한다")
    void settleMaturity_shouldSucceed_whenSubscriptionIsMaturedAndActive() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 9, 1);
        LocalDate maturityDate = startDate.plusDays(12);
        when(timeProvider.today()).thenReturn(maturityDate);

        SavingSubscription subscription = createMockSubscription();
        // 테스트에 필요한 stubbing을 '여기서' 직접 정의
        when(subscription.getStatus()).thenReturn(SubscriptionStatus.ACTIVE);
        when(subscription.getDates()).thenReturn(new ServiceDates(startDate, maturityDate));
        when(subscription.getTerm()).thenReturn(new TermMonths(12));
        when(subscription.getProductSnapshotId()).thenReturn(PRODUCT_SNAPSHOT_ID);

        when(subscriptionRepo.findByIdAndUserId(SUBSCRIPTION_ID, USER_ID)).thenReturn(Optional.of(subscription));

        BigDecimal principal = new BigDecimal("1200000");
        when(paymentRepo.calculateTotalPaidAmount(SUBSCRIPTION_ID)).thenReturn(principal);
        when(paymentRepo.countBySubscriptionIdAndStatus(SUBSCRIPTION_ID, SavingPaymentHistory.PaymentStatus.MISSED)).thenReturn(0L);

        BigDecimal interestRate = new BigDecimal("5.0");
        SavingProductOptionSnapshot optionSnapshot = mock(SavingProductOptionSnapshot.class);
        when(optionSnapshot.getIntrRate()).thenReturn(interestRate);
        when(optionRepo.findFirstByProductSnapshotIdAndSaveTrmMonthsOrderByIntrRateDesc(anyLong(), anyInt()))
                .thenReturn(optionSnapshot);

        // When
        MaturitySettlementService.PayoutQuote result = maturitySettlementService.settleMaturity(USER_ID, SUBSCRIPTION_ID);

        // Then
        BigDecimal expectedInterest = principal.multiply(interestRate).divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN);
        BigDecimal expectedTotal = principal.add(expectedInterest);

        assertThat(result.total()).isEqualByComparingTo(expectedTotal);
        verify(savingTxnService, times(1)).processSavingMaturity(eq(USER_ID), anyString(), eq(expectedTotal), eq(SUBSCRIPTION_ID));
        verify(subscription, times(1)).mature();
        verify(subscriptionRepo, times(1)).save(subscription);
    }

    @Test
    @DisplayName("실패: 만기일이 아직 도래하지 않은 적금은 정산할 수 없다")
    void settleMaturity_shouldThrowException_whenNotMaturedYet() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 9, 1);
        LocalDate maturityDate = startDate.plusDays(12);
        LocalDate today = startDate.plusDays(11);
        when(timeProvider.today()).thenReturn(today);

        SavingSubscription subscription = createMockSubscription();
        // 이 테스트에서는 getDates() 호출만 필요함
        when(subscription.getDates()).thenReturn(new ServiceDates(startDate, maturityDate));
        when(subscriptionRepo.findByIdAndUserId(SUBSCRIPTION_ID, USER_ID)).thenReturn(Optional.of(subscription));

        // When & Then
        assertThrows(SavingExceptions.SavingSubscriptionInvalidStateException.class,
                () -> maturitySettlementService.settleMaturity(USER_ID, SUBSCRIPTION_ID)
        );
        verify(savingTxnService, never()).processSavingMaturity(anyLong(), anyString(), any(), anyLong());
    }

    @Test
    @DisplayName("실패: 이미 만기 처리된 적금은 다시 정산할 수 없다")
    void settleMaturity_shouldThrowException_whenSubscriptionIsNotActive() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 9, 1);
        LocalDate maturityDate = startDate.plusDays(12);
        when(timeProvider.today()).thenReturn(maturityDate);

        SavingSubscription subscription = createMockSubscription();
        // 이 테스트에서는 getDates()와 getStatus() 호출이 필요함
        when(subscription.getDates()).thenReturn(new ServiceDates(startDate, maturityDate));
        when(subscription.getStatus()).thenReturn(SubscriptionStatus.MATURED);
        when(subscriptionRepo.findByIdAndUserId(SUBSCRIPTION_ID, USER_ID)).thenReturn(Optional.of(subscription));

        // When & Then
        assertThrows(SavingExceptions.SavingSubscriptionInvalidStateException.class,
                () -> maturitySettlementService.settleMaturity(USER_ID, SUBSCRIPTION_ID)
        );
    }

    @Test
    @DisplayName("만기 예정 목록 조회: 만기일이 지났고 ACTIVE 상태인 적금만 목록에 포함된다")
    void listPendingMaturities_shouldReturnOnlyActiveAndMaturedSubscriptions() {
        // Given
        LocalDate today = LocalDate.of(2025, 9, 13);
        when(timeProvider.today()).thenReturn(today);

        // 1. 포함될 적금 (만기일 지남, ACTIVE)
        SavingSubscription pendingSub = createMockSubscription();
        when(pendingSub.getId()).thenReturn(SUBSCRIPTION_ID); // DTO 비교를 위해 ID stubbing 필요
        when(pendingSub.getDates()).thenReturn(new ServiceDates(today.minusDays(12), today));

        // 2. 포함되지 않을 적금 (만기일 안 지남)
        SavingSubscription notMaturedSub = mock(SavingSubscription.class);
        when(notMaturedSub.getDates()).thenReturn(new ServiceDates(today.minusDays(10), today.plusDays(2)));

        List<SavingSubscription> activeSubs = List.of(pendingSub, notMaturedSub);
        when(subscriptionRepo.findByUserIdAndStatus(USER_ID, SubscriptionStatus.ACTIVE)).thenReturn(activeSubs);

        // computeQuote 메서드 내부에서 필요한 stubbing
        when(pendingSub.getProductSnapshotId()).thenReturn(PRODUCT_SNAPSHOT_ID);
        when(pendingSub.getTerm()).thenReturn(new TermMonths(12));
        when(paymentRepo.calculateTotalPaidAmount(any())).thenReturn(BigDecimal.TEN);
        SavingProductOptionSnapshot optionSnapshot = mock(SavingProductOptionSnapshot.class);
        when(optionSnapshot.getIntrRate()).thenReturn(BigDecimal.ONE);
        when(optionRepo.findFirstByProductSnapshotIdAndSaveTrmMonthsOrderByIntrRateDesc(anyLong(), anyInt()))
                .thenReturn(optionSnapshot);
        when(productSnapshotRepo.findById(any())).thenReturn(Optional.empty());

        // When
        List<MaturitySettlementService.PendingMaturityDto> result = maturitySettlementService.listPendingMaturities(USER_ID);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).subscriptionId()).isEqualTo(pendingSub.getId());
    }

    // === Helper Method ===
    // 헬퍼 메서드는 순수하게 Mock 객체를 생성하는 역할만 담당
    private SavingSubscription createMockSubscription() {
        SavingSubscription mockSub = mock(SavingSubscription.class);
        // 테스트에 공통적으로 필요한 최소한의 stubbing만 남기거나 비워둠
        lenient().when(mockSub.getId()).thenReturn(SUBSCRIPTION_ID);
        return mockSub;
    }
}