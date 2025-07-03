package com.nhnacademy.illuwa.domain.pointpolicy.entity;

import com.nhnacademy.illuwa.domain.pointpolicy.entity.enums.PointValueType;
import com.nhnacademy.illuwa.domain.pointpolicy.entity.enums.PolicyStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "point_policy")
public class PointPolicy {
    @Id
    @Column(name = "policy_key", unique = true)
    private String policyKey;
    //join_point 회원가입 포인트
    //review_point 리뷰 포인트
    //photo_review_point 포토리뷰 포인트
    //book_default_rate 도서 기본 적립률

    @Column(name = "point_value", precision = 10, scale = 2, nullable = false)
    private BigDecimal value;

    @Column(name = "value_type", nullable = false)
    private PointValueType valueType;

    @Column(name = "description", nullable = false)
    private String description;

    @Builder.Default
    @Column(name = "status", nullable = false)
    private PolicyStatus status = PolicyStatus.ACTIVE;

    public void changeValue(BigDecimal value) {
        this.value = value;
    }

    public void changeValueType(PointValueType valueType) {
        this.valueType = valueType;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeStatus(PolicyStatus status) {
        this.status = status;
    }

}

