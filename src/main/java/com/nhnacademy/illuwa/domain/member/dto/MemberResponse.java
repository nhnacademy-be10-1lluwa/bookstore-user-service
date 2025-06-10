package com.nhnacademy.illuwa.domain.member.dto;

import com.nhnacademy.illuwa.domain.member.entity.enums.Grade;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    private int memberId;
    private String name;
    private String email;
    private Role role;
    private String phoneNumber;
    private Grade grade;
    private BigDecimal point;
    private Status status;
    private LocalDateTime lastLoginAt;
}
