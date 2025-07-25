package com.nhnacademy.illuwa.domain.point.pointhistory.dto;

import com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums.PointHistoryType;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums.PointReason;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PointHistoryRequest {
    private long memberId;
    private PointHistoryType type;
    private PointReason reason;
    private BigDecimal amount;
    private BigDecimal balance;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
