package com.freedom.saving.application.read;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 상세 응답용 dto
 */
public class SavingProductDetail {

    private Long productSnapshotId;
    private String productName;
    private String bankName;
    private String joinWay;
    private String specialCondition;
    private String joinDeny;
    private String joinMember;
    private String etcNote;
    private String aiSummary; // 자리만 확보
    private LocalDateTime fetchedAt;
    private List<SavingProductOptionItem> options = new ArrayList<SavingProductOptionItem>();

    public Long getProductSnapshotId() { return productSnapshotId; }
    public String getProductName() { return productName; }
    public String getBankName() { return bankName; }
    public String getJoinWay() { return joinWay; }
    public String getSpecialCondition() { return specialCondition; }
    public String getJoinDeny() { return joinDeny; }
    public String getJoinMember() { return joinMember; }
    public String getEtcNote() { return etcNote; }
    public String getAiSummary() { return aiSummary; }
    public LocalDateTime getFetchedAt() { return fetchedAt; }
    public List<SavingProductOptionItem> getOptions() { return options; }

    public void setProductSnapshotId(Long productSnapshotId) { this.productSnapshotId = productSnapshotId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public void setJoinWay(String joinWay) { this.joinWay = joinWay; }
    public void setSpecialCondition(String specialCondition) { this.specialCondition = specialCondition; }
    public void setJoinDeny(String joinDeny) { this.joinDeny = joinDeny; }
    public void setJoinMember(String joinMember) { this.joinMember = joinMember; }
    public void setEtcNote(String etcNote) { this.etcNote = etcNote; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
    public void setFetchedAt(LocalDateTime fetchedAt) { this.fetchedAt = fetchedAt; }
    public void setOptions(List<SavingProductOptionItem> options) { this.options = options; }
}
