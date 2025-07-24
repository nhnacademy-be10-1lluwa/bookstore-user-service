package com.nhnacademy.illuwa.domain.point.pointpolicy.dto;

import com.nhnacademy.illuwa.domain.point.pointpolicy.entity.enums.PointValueType;
import com.nhnacademy.illuwa.domain.point.pointpolicy.entity.enums.PolicyStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class PointPolicyResponse {
    private String policyKey;

    private PolicyStatus status;

    private BigDecimal value;

    private PointValueType valueType;

    private String description;
}
