package com.nhnacademy.illuwa.domain.pointhistory.dto;

import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointHistoryType;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryResponse {
    private int amount;
    private PointReason reason;
    private PointHistoryType type;
    private LocalDateTime createdAt;
}
