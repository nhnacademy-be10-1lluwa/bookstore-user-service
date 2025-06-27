package com.nhnacademy.illuwa.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberPointResponse {
    long memberId;
    BigDecimal point;
}
