package com.nhnacademy.illuwa.domain.message.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCodeRequest {
    Long memberId;
    String code;
}
