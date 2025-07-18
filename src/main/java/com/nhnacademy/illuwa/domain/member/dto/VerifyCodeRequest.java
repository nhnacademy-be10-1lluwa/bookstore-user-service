package com.nhnacademy.illuwa.domain.member.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCodeRequest {
    String email;
    String code;
}
