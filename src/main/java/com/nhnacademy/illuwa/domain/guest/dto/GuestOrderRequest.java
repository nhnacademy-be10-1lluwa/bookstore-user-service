package com.nhnacademy.illuwa.domain.guest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GuestOrderRequest {
    @NotBlank
    String guestId;

    @Positive
    long orderId;

    @NotBlank
    String orderNumber;

    @NotBlank
    String orderPassword;

    @NotBlank
    String name;

    @Email
    @NotBlank
    String email;

    @Pattern(regexp = "^010-\\d{3,4}-\\d{4}$",
            message = "휴대폰 번호는 010으로 시작하는 11자리 숫자와 '-'로 구성되어야 합니다.")
    String contact;
}
