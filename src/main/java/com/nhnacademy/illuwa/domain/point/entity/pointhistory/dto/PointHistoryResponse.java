package com.nhnacademy.illuwa.domain.point.entity.pointhistory.dto;

import com.nhnacademy.illuwa.domain.point.entity.pointhistory.enums.PointHistoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryResponse {
    private int amount;
    private String reason;
    private PointHistoryType type;
    private LocalDateTime createdAt;
}
