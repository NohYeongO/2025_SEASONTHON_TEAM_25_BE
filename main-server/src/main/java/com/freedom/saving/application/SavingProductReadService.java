package com.freedom.saving.application;

import com.freedom.common.exception.custom.SavingProductNotFoundException;
import com.freedom.saving.application.read.SavingProductDetail;
import com.freedom.saving.application.read.SavingProductListItem;
import com.freedom.saving.application.read.SavingProductOptionItem;
import com.freedom.saving.domain.SavingProductOptionSnapshot;
import com.freedom.saving.domain.SavingProductSnapshot;
import com.freedom.saving.infra.snapshot.SavingProductOptionSnapshotJpaRepository;
import com.freedom.saving.infra.snapshot.SavingProductSnapshotJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 읽기 전용 유스케이스: 상품 목록/상세 조회
 * 도메인 엔티티를 외부로 직접 노출하지 않고, application DTO(SavingProductListItem/Detail)로 매핑해 반환
 */
@Service
@Transactional(readOnly = true)
public class SavingProductReadService {

    private final SavingProductSnapshotJpaRepository productRepo;
    private final SavingProductOptionSnapshotJpaRepository optionRepo;

    public SavingProductReadService(SavingProductSnapshotJpaRepository productRepo,
                                    SavingProductOptionSnapshotJpaRepository optionRepo) {
        this.productRepo = productRepo;
        this.optionRepo = optionRepo;
    }

    /**
     * [목록] 인기순 적금 상품 조회
     *
     * 정렬 기준: 최신 스냅샷 중 subscriberCount 내림차순
     * 프론트 요구에 따라 전체 리스트를 반환한다.
     */
    public Page<SavingProductListItem> getPopularSavingProducts(int page, int size) {
        // 1) 최신 스냅샷 전체 조회(가입자수 내림차순)
        List<SavingProductSnapshot> contents = productRepo.findAllLatestOrderBySubscriberCountDesc();

        // 2) 엔티티 -> 목록 DTO
        List<SavingProductListItem> items = new ArrayList<SavingProductListItem>();
        for (SavingProductSnapshot s : contents) {
            SavingProductListItem item = new SavingProductListItem();
            item.setProductSnapshotId(s.getId());
            item.setProductName(s.getFinPrdtNm());
            item.setBankName(s.getKorCoNm());

            // DTO에 해당 필드가 아직 존재한다면 null/빈값으로 둔다.
            item.setAiSummary(s.getAiSummary() != null ? s.getAiSummary() : "");

            items.add(item);
        }

        // 3) Page 래핑 반환 (전체)
        return new PageImpl<SavingProductListItem>(items, PageRequest.of(0, items.isEmpty() ? 1 : items.size()), items.size());
    }

    /**
     * [상세] 상품 1건 상세 조회(헤더 + 옵션 목록)
     */
    public SavingProductDetail getDetail(Long productSnapshotId) {
        // 1) 스냅샷 단건 조회
        Optional<SavingProductSnapshot> optional = productRepo.findById(productSnapshotId);
        if (!optional.isPresent()) {
            throw new SavingProductNotFoundException(productSnapshotId);
        }
        SavingProductSnapshot s = optional.get();

        // 2) 옵션 목록 조회
        List<SavingProductOptionSnapshot> options =
                optionRepo.findByProductSnapshotIdOrderBySaveTrmMonthsAsc(productSnapshotId);

        // 3) 엔티티 -> 상세 DTO 매핑
        SavingProductDetail detail = new SavingProductDetail();
        detail.setProductSnapshotId(s.getId());
        detail.setProductName(s.getFinPrdtNm());
        detail.setBankName(s.getKorCoNm());
        detail.setJoinWay(s.getJoinWay());
        detail.setMaturityInterest(s.getMtrtInt()); // 만기 후 이자율
        detail.setSpecialCondition(s.getSpclCnd());
        detail.setJoinDeny(s.getJoinDeny());
        detail.setJoinMember(s.getJoinMember());
        detail.setMaxLimit(s.getMaxLimit()); // 최고한도
        detail.setEtcNote(s.getEtcNote());
        detail.setFetchedAt(s.getFetchedAt());

        detail.setAiSummary(s.getAiSummary() != null ? s.getAiSummary() : "");

        List<SavingProductOptionItem> optionItems = new ArrayList<SavingProductOptionItem>();
        for (SavingProductOptionSnapshot o : options) {
            SavingProductOptionItem oi = new SavingProductOptionItem();
            oi.setTermMonths(o.getSaveTrmMonths());
            oi.setRate(o.getIntrRate());
            oi.setRatePreferential(o.getIntrRate2());
            oi.setRateType(o.getIntrRateType());
            oi.setRateTypeName(o.getIntrRateTypeNm());
            oi.setReserveType(o.getRsrvType());
            oi.setReserveTypeName(o.getRsrvTypeNm());

            optionItems.add(oi);
        }
        detail.setOptions(optionItems);

        return detail;
    }
}
