package com.nhnacademy.illuwa.domain.point.pointhistory.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PointAfterOrderRequest {
    long memberId;
    BigDecimal price;
}
