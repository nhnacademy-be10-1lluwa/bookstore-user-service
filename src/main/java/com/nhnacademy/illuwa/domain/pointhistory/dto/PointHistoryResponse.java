package com.nhnacademy.illuwa.domain.pointhistory.dto;

import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointHistoryType;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryResponse {
    private PointHistoryType type;
    private PointReason reason;
    private BigDecimal amount;
    private BigDecimal balance;
    private LocalDateTime createdAt;
}
