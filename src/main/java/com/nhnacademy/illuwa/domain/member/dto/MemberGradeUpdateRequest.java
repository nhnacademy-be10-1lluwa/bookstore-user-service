package com.nhnacademy.illuwa.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberGradeUpdateRequest {
    private long memberId;
    private List<BigDecimal> netOrderAmount;
}
