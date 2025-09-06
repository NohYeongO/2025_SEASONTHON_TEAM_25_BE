package com.freedom.saving.application.read;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 목록 응답용 dto
 * 엔티티를 직접 노출하지 않고 필요한 필드만 담기
 */
@Getter
@Setter
public class SavingProductListItem {
    private Long productSnapshotId;
    private String productName;
    private String bankName;
    private String aiSummary;

}
