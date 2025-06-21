package com.nhnacademy.illuwa.domain.member.dto;

import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponse {
    private long memberId;
    private String name;
    private LocalDate birth;
    private String email;
    private Role role;
    private String contact;
    private String gradeName;
    private BigDecimal point;
    private Status status;
    private LocalDateTime lastLoginAt;
}
