package com.nhnacademy.illuwa.domain.member.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRegisterRequest {
    private String name;
    private LocalDate birth;
    private String email;
    private String password;
    private String contact;
}

