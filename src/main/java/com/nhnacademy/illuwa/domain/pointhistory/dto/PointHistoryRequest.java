package com.nhnacademy.illuwa.domain.pointhistory.dto;

import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointHistoryType;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryRequest {
    private long memberId;
    private PointHistoryType type;
    private PointReason reason;
    private BigDecimal amount;
    private BigDecimal balance;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
