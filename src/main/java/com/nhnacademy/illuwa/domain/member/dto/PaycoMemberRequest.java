package com.nhnacademy.illuwa.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaycoMemberRequest {
    private String idNo;  //paycoId
    private String name;
    private String email;
    private String mobile;
    private String birthdayMMdd;
    //MMdd로 페이코에서 받아와도 회원가입 시 LocalDate 완전한 값으로 덮어씌울 것
}
