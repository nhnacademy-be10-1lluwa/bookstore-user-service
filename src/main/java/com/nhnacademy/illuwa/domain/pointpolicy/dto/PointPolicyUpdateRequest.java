package com.nhnacademy.illuwa.domain.pointpolicy.dto;

import com.nhnacademy.illuwa.domain.pointpolicy.entity.enums.PointValueType;
import com.nhnacademy.illuwa.domain.pointpolicy.entity.enums.PolicyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
public class PointPolicyUpdateRequest {
    private PolicyStatus status;

    @NotNull(message = "포인트 값은 필수입니다.")
    private BigDecimal value;

    @NotNull(message = "포인트 타입은 RATE/AMOUNT 필수입니다.")
    private PointValueType valueType;

    private String description;
}
