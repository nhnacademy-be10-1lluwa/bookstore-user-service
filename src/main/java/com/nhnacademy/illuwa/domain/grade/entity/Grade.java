package com.nhnacademy.illuwa.domain.grade.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "grade")
@Getter
@NoArgsConstructor
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_id")
    private long gradeId;

    // BASIC, GOLD, ROYAL, PLATINUM;
    @Column(name = "grade_name", unique = true)
    private String gradeName;

    @Column(name = "priority", nullable = false)
    private long priority;

    // 포인트적립율
    @Column(name = "point_rate", nullable = false)
    private BigDecimal pointRate;

    //최소금액
    @Column(name = "min_amount", nullable = false)
    private BigDecimal minAmount;

    //상한금액
    @Column(name = "max_amount")
    private BigDecimal maxAmount;

    //본인의 필드 값만 사용하는 순수 판단 로직   -> 컬렉션 돌면서 비교하는건 서비스차원
    public boolean inRange(BigDecimal netOrderAmount) {
        boolean overMin = netOrderAmount.compareTo(minAmount) >= 0;
        boolean underMax = (maxAmount == null || netOrderAmount.compareTo(maxAmount) < 0);
        return overMin && underMax;
    }

    public String getDisplayName() {
        return switch (gradeName) {
            case "BASIC" -> "Basic";
            case "GOLD" -> "Gold";
            case "ROYAL" -> "Royal";
            case "PLATINUM" -> "Platinum";
            default -> gradeName;
        };
    }

    @Builder
    public Grade(String gradeName, long priority, BigDecimal minAmount, BigDecimal maxAmount, BigDecimal pointRate){
        this.gradeName = gradeName;
        this.priority = priority;
        this.pointRate = pointRate;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }
}
