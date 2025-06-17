package com.nhnacademy.illuwa.domain.member.dto;

import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateRequest {
    private String name;

    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}")
    private String password;

    @Pattern(regexp = "^010-\\d{3,4}-\\d{4}$")
    private String contact;
}
