package com.nhnacademy.illuwa.domain.guest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GuestOrderRequest {
    long orderId;

    String orderNumber;
    String orderPassword;

    String name;
    String email;
    String contact;
}
