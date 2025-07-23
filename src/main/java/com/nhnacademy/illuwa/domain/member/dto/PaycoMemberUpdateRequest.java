package com.nhnacademy.illuwa.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
public class PaycoMemberUpdateRequest {
    @NotBlank(message = "이름은 필수 입력값입니다")
    private String name;
    @Past
    private LocalDate birth;

    @Email(message = "유효한 이메일 형식이 아닙니다")
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;

    @Pattern(regexp = "^010-[1-9]\\d{3}-[1-9]\\d{3}$",
            message = "휴대폰 번호는 010으로 시작하는 11자리 숫자와 '-'로 구성되어야 합니다.")
    private String contact;
}
