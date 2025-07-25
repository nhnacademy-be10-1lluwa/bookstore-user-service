package com.nhnacademy.illuwa.domain.point.pointpolicy.dto;

import com.nhnacademy.illuwa.domain.point.pointpolicy.entity.enums.PointValueType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PointPolicyCreateRequest {
    @NotBlank(message = "포인트 정책 키 설정은 필수입니다.")
    private String policyKey;

    @NotNull(message = "포인트 값은 필수입니다.")
    private BigDecimal value;

    @NotNull(message = "포인트 타입은 RATE/AMOUNT 필수입니다.")
    private PointValueType valueType;

    private String description;
}
