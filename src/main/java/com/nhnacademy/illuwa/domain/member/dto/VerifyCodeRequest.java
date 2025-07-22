package com.nhnacademy.illuwa.domain.member.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCodeRequest {
    String contact;
    String code;
}
