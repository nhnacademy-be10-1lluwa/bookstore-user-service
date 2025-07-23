package com.nhnacademy.illuwa.domain.point.pointhistory.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class UsedPointRequest {
    long memberId;
    BigDecimal usedPoint;
}
