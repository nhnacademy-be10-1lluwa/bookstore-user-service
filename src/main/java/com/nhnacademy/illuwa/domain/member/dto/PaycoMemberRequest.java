package com.nhnacademy.illuwa.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaycoMemberRequest {
    private String name;
    private String email;
    private String mobile;
    private String birthdayMMdd;
}
