package com.nhnacademy.illuwa.domain.pointhistory.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PointAfterOrderRequest {
    long memberId;
    BigDecimal netOrderAmount;
}
