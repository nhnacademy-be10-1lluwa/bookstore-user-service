package com.nhnacademy.illuwa.domain.Point.entity.pointpolicy;

import com.nhnacademy.illuwa.domain.Point.entity.pointpolicy.enums.PointValueType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
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

    private String description;

}

