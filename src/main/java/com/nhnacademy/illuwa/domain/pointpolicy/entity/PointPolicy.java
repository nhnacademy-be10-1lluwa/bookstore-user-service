package com.nhnacademy.illuwa.domain.pointpolicy.entity;

import com.nhnacademy.illuwa.domain.pointpolicy.entity.enums.PointValueType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "point_policy")
public class PointPolicy {
    @Id
    @Column(name = "policy_key")
    private String policyKey;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal value;

    @Column(name = "value_type", nullable = false)
    private PointValueType valueType;

    @Column(name = "description", nullable = false)
    private String description;
}

