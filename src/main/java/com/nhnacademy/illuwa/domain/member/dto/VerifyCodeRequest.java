package com.nhnacademy.illuwa.domain.member.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCodeRequest {
    String contact;
    String code;
}
