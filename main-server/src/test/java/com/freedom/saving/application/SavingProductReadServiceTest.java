package com.freedom.saving.application;

import com.freedom.saving.application.read.SavingProductListItem;
import com.freedom.saving.domain.SavingProductSnapshot;
import com.freedom.saving.domain.shapshot.SavingProductSnapshotDraft;
import com.freedom.saving.infra.snapshot.SavingProductOptionSnapshotJpaRepository;
import com.freedom.saving.infra.snapshot.SavingProductSnapshotJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class) // Mockito 기능을 JUnit5에서 사용
class SavingProductReadServiceTest {

    @InjectMocks // 테스트 대상 클래스. @Mock 객체들이 여기에 주입됩니다.
    private SavingProductReadService savingProductReadService;

    @Mock // 가짜(Mock) 객체로 만들 의존성
    private SavingProductSnapshotJpaRepository productRepo;

    // 이 테스트에서는 사용되지 않지만, 클래스의 의존성이므로 Mock으로 선언해 줍니다.
    @Mock
    private SavingProductOptionSnapshotJpaRepository optionRepo;

    @Test
    @DisplayName("인기순 상품 목록 조회 시, 가입자 수(subscriberCount)가 많은 순서대로 정렬되어 반환된다")
    void getPopularSavingProducts_returnsSortedBySubscriberCountDesc() {
        // given (주어진 상황)
        // 1. 가입자 수가 무작위 순서인 3개의 가짜 SavingProductSnapshot 엔티티를 생성합니다.
        SavingProductSnapshot productA = createSnapshot(1L, "보통 상품", 50L);
        SavingProductSnapshot productB = createSnapshot(2L, "인기 상품", 100L);
        SavingProductSnapshot productC = createSnapshot(3L, "비인기 상품", 10L);

        // 2. Repository의 정렬 메서드가 호출될 때, 가입자 수 내림차순으로 정렬된 리스트를 반환하도록 Mocking 합니다.
        List<SavingProductSnapshot> sortedSnapshots = List.of(productB, productA, productC);
        given(productRepo.findAllLatestOrderBySubscriberCountDesc()).willReturn(sortedSnapshots);

        // when (무엇을 할 때)
        Page<SavingProductListItem> resultPage = savingProductReadService.getPopularSavingProducts(0, 10);

        // then (이런 결과가 나와야 한다)
        // 1. 결과가 null이 아닌지 확인합니다.
        assertThat(resultPage).isNotNull();

        // 2. 반환된 DTO 리스트를 추출합니다.
        List<SavingProductListItem> resultList = resultPage.getContent();

        // 3. 리스트의 크기가 3개인지 확인합니다.
        assertThat(resultList).hasSize(3);

        // 4. 리스트의 순서가 subscriberCount 내림차순 (productB -> productA -> productC)과 일치하는지 검증합니다.
        assertThat(resultList.get(0).getProductName()).isEqualTo("인기 상품");
        assertThat(resultList.get(1).getProductName()).isEqualTo("보통 상품");
        assertThat(resultList.get(2).getProductName()).isEqualTo("비인기 상품");

        // 5. Repository의 특정 메서드가 정확히 1번 호출되었는지 검증합니다.
        verify(productRepo, times(1)).findAllLatestOrderBySubscriberCountDesc();
    }

    @Test
    @DisplayName("인기순 상품 목록 조회 시, 상품이 하나도 없으면 빈 페이지를 반환한다")
    void getPopularSavingProducts_returnsEmptyPageWhenNoProductsExist() {
        // given
        // Repository가 빈 리스트를 반환하도록 설정합니다.
        given(productRepo.findAllLatestOrderBySubscriberCountDesc()).willReturn(List.of());

        // when
        Page<SavingProductListItem> resultPage = savingProductReadService.getPopularSavingProducts(0, 10);

        // then
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getContent()).isEmpty(); // 내용이 비어있는지 확인
        verify(productRepo, times(1)).findAllLatestOrderBySubscriberCountDesc();
    }

    private SavingProductSnapshot createSnapshot(Long id, String productName, Long subscriberCount) {
        // dclsMonth, finCoNo, finPrdtCd는 null이거나 비어있으면 안 됩니다.
        SavingProductSnapshotDraft dummyDraft = new SavingProductSnapshotDraft(
                "202509", "0010001", "PDT-" + id, // 필수 식별자 값
                "테스트은행", productName, "", "", "", "", "", "",
                1000000, "", "", ""
        );

        SavingProductSnapshot snapshot = SavingProductSnapshot.from(
                dummyDraft, // 더미 객체 전달
                true,
                LocalDateTime.now()
        );

        // ReflectionTestUtils를 사용해 테스트에 필요한 값을 덮어씁니다.
        ReflectionTestUtils.setField(snapshot, "id", id);
        ReflectionTestUtils.setField(snapshot, "subscriberCount", subscriberCount);
        // finPrdtNm은 이미 생성자에서 productName으로 설정되었으므로 생략 가능

        return snapshot;
    }
}
