package com.freedom.saving.application.read;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 상세 응답용 dto
 */
@Getter
@Setter
public class SavingProductDetail {

    private Long productSnapshotId;
    private String productName;
    private String bankName;
    private String joinWay;
    private String maturityInterest; // 만기 후 이자율
    private String specialCondition;
    private String joinDeny;
    private String joinMember;
    private Integer maxLimit; // 최고한도
    private String etcNote;
    private String aiSummary; // 자리만 확보
    private LocalDateTime fetchedAt;
    private List<SavingProductOptionItem> options = new ArrayList<SavingProductOptionItem>();
}
