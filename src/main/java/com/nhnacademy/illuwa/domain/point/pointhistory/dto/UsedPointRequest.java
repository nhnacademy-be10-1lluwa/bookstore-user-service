package com.nhnacademy.illuwa.domain.point.pointhistory.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UsedPointRequest {
    long memberId;
    BigDecimal usedPoint;
}
