package com.nhnacademy.illuwa.domain.point.pointhistory.dto;

import com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums.PointHistoryType;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums.PointReason;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryResponse {
    private long memberId;
    private PointHistoryType type;
    private PointReason reason;
    private BigDecimal amount;
    private BigDecimal balance;
    private LocalDateTime createdAt;
}
