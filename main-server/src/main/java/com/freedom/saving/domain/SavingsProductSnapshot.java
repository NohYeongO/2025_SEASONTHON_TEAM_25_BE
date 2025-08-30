package com.freedom.saving.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


/**
 * "가입 시점에 고정되는 상품 기본정보" 스냅샷
 * 외부 API 데이터가 시간이 지나 변경되어도, 가입 당시 상태를 보존하기 위해 스냅샷을 둔다.
 */
@Getter
@Entity
@Table(
        name = "saving_product_snapshot",
        uniqueConstraints = @UniqueConstraint(name = "uk_product_month", columnNames = {"fin_co_no","fin_prdt_cd","dcls_month"}),
        indexes = @Index(name = "idx_product_code", columnList = "fin_co_no, fin_prdt_cd")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavingsProductSnapshot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fin_prdt_cd", nullable = false, length = 32)
    private String finPrdtCd;

    @Column(name = "fin_co_no", nullable = false, length = 16)
    private String finCoNo;

    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    @Column(name = "product_name", nullable = false, length = 150)
    private String productName;

    @Column(name = "dcls_month", nullable = false, length = 6) // YYYYMM
    private String dclsMonth;

    @Column(name = "join_deny", length = 4)
    private String joinDeny;

    @Column(name = "join_member", length = 200)
    private String joinMember;

    @Column(name = "join_way", length = 300)
    private String joinWay;

    @Lob @Column(name = "mtrt_int")
    private String mtrtInt;

    @Lob @Column(name = "spcl_cnd")
    private String spclCnd;

    @Lob @Column(name = "etc_note")
    private String etcNote;

    @Column(name = "dcls_strt_day", length = 8)
    private String dclsStrtDay; // YYYYMMDD

    @Column(name = "dcls_end_day", length = 8)
    private String dclsEndDay;  // YYYYMMDD or null

    @Column(name = "fin_co_subm_day", length = 20)
    private String finCoSubmDay; // 원문 그대로 보존(YYYYMMDDHHmm 형태 등)

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // 옵션 스냅샷: 부모-자식 관계. 상품 삭제를 제한하려면 서비스/DDL에서 제어.
    @OneToMany(mappedBy = "productSnapshot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavingsOptionSnapshot> options = new ArrayList<>();

    private SavingsProductSnapshot(
            String finPrdtCd, String finCoNo,
            String bankName, String productName,
            String dclsMonth, String joinDeny, String joinMember, String joinWay,
            String mtrtInt, String spclCnd, String etcNote,
            String dclsStrtDay, String dclsEndDay, String finCoSubmDay
    ) {
        this.finPrdtCd = finPrdtCd;
        this.finCoNo = finCoNo;
        this.bankName = bankName;
        this.productName = productName;
        this.dclsMonth = dclsMonth;
        this.joinDeny = joinDeny;
        this.joinMember = joinMember;
        this.joinWay = joinWay;
        this.mtrtInt = mtrtInt;
        this.spclCnd = spclCnd;
        this.etcNote = etcNote;
        this.dclsStrtDay = dclsStrtDay;
        this.dclsEndDay = dclsEndDay;
        this.finCoSubmDay = finCoSubmDay;
    }

    // 세터를 두지 않고, 생성 시점에만 필수 값 고정
    public static SavingsProductSnapshot of(
            String finPrdtCd, String finCoNo,
            String bankName, String productName,
            String dclsMonth, String joinDeny, String joinMember, String joinWay,
            String mtrtInt, String spclCnd, String etcNote,
            String dclsStrtDay, String dclsEndDay, String finCoSubmDay
    ) {
        return new SavingsProductSnapshot(
                finPrdtCd, finCoNo, bankName, productName, dclsMonth,
                joinDeny, joinMember, joinWay, mtrtInt, spclCnd, etcNote,
                dclsStrtDay, dclsEndDay, finCoSubmDay
        );
    }

    // 연관관계 편의 메서드: 부모-자식 양방향 일관성 보장
    public void addOption(SavingsOptionSnapshot option) {
        option.bindTo(this);
        this.options.add(option);
    }
}
