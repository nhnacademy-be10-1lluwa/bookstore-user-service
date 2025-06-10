package com.nhnacademy.illuwa.domain.member.dto;

import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRegisterRequest {
    private String name;
    private LocalDate birth;
    private String email;
    private String password;
    private Role role;
    private String phoneNumber;
}

