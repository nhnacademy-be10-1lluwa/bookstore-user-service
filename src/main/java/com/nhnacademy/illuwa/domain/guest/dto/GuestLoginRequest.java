package com.nhnacademy.illuwa.domain.guest.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestLoginRequest {
    @NotEmpty
    long orderId;
    @NotEmpty
    String orderPassword;
}
