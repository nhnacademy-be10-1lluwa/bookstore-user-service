package com.nhnacademy.illuwa.domain.message.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InactiveVerificationRequest {
    Long memberId;
    String content;
}
