package com.nhnacademy.illuwa.domain.Point.entity.pointpolicy.dto;

import com.nhnacademy.illuwa.domain.Point.entity.pointpolicy.enums.PointValueType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PointPolicyUpdateRequest {
    private BigDecimal value;
    private PointValueType valueType;
    private String description;
}
