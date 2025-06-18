package com.nhnacademy.illuwa.domain.pointpolicy.dto;

import com.nhnacademy.illuwa.domain.pointpolicy.entity.enums.PointValueType;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointPolicyResponse {
    private String policyKey;

    private BigDecimal value;

    private PointValueType valueType;

    private String description;
}
