package com.nhnacademy.illuwa.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaycoMemberRequest {
    private String idNo;  //paycoId
    private String name;
    private String email;
    private String mobile;
    private String birthdayMMdd;
}
