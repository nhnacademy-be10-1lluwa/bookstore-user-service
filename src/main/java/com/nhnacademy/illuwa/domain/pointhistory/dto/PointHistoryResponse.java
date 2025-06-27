package com.nhnacademy.illuwa.domain.pointhistory.dto;

import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointHistoryType;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryResponse {
    private BigDecimal amount;
    private PointReason reason;
    private PointHistoryType type;
    private LocalDateTime createdAt;
}
