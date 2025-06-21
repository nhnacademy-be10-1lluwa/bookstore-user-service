package com.nhnacademy.illuwa.domain.grade.entity;

import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "grade_name", unique = true)
    private GradeName gradeName;

    @Column(name = "priority", nullable = false)
    private long priority;

    @Column(name = "point_rate", nullable = false)
    private BigDecimal pointRate;

    @Column(name = "min_amount", nullable = false)
    private BigDecimal minAmount;

    @Column(name = "max_amount")
    private BigDecimal maxAmount;

    public boolean inRange(BigDecimal netOrderAmount) {
        boolean overMin = netOrderAmount.compareTo(minAmount) >= 0;
        boolean underMax = (maxAmount == null || netOrderAmount.compareTo(maxAmount) < 0);
        return overMin && underMax;
    }

    @Builder
    public Grade(GradeName gradeName, long priority, BigDecimal minAmount, BigDecimal maxAmount, BigDecimal pointRate){
        this.gradeName = gradeName;
        this.priority = priority;
        this.pointRate = pointRate;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }
}
