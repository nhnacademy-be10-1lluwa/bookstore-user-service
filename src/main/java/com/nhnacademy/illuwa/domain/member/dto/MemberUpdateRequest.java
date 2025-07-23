package com.nhnacademy.illuwa.domain.member.dto;

import com.nhnacademy.illuwa.common.annotation.ConditionalPattern;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberUpdateRequest {
    private String name;

    @ConditionalPattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}")
    private String password;

    @Pattern(regexp = "^010-[1-9][0-9]{3}-[1-9][0-9]{3}$", message = "전화번호는 010-0000-0000 형식이어야 합니다.")
    private String contact;
}
