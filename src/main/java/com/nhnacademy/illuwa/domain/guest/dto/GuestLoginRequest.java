package com.nhnacademy.illuwa.domain.guest.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GuestLoginRequest {
    @NotEmpty
    String orderNumber;
    @NotEmpty
    String orderPassword;
}
