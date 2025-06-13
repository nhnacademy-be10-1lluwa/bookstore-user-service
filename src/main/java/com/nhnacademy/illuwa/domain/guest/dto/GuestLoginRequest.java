package com.nhnacademy.illuwa.domain.guest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestLoginRequest {
    long orderId;
    String orderPassword;
}
