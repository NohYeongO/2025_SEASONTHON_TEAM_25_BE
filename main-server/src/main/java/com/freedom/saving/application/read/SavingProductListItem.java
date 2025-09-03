package com.freedom.saving.application.read;

import java.math.BigDecimal;

/**
 * 목록 응답용 dto
 * 엔티티를 직접 노출하지 않고 필요한 필드만 담기
 */
public class SavingProductListItem {
    private Long productSnapshotId;
    private String productName;
    private String bankName;
    private BigDecimal bestRate; // 페이지 내 일괄 집계해서 채움
    private String aiSummary;    // 자리만 확보

    public Long getProductSnapshotId() { return productSnapshotId; }
    public String getProductName() { return productName; }
    public String getBankName() { return bankName; }
    public BigDecimal getBestRate() { return bestRate; }
    public String getAiSummary() { return aiSummary; }

    public void setProductSnapshotId(Long productSnapshotId) { this.productSnapshotId = productSnapshotId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public void setBestRate(BigDecimal bestRate) { this.bestRate = bestRate; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
}
