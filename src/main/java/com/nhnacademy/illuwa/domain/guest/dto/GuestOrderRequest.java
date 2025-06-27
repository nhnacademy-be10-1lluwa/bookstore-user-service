package com.nhnacademy.illuwa.domain.guest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GuestOrderRequest {
    long orderId;
    //비회원 개인정보
    String orderNumber;
    String guestName;
    String guestEmail;
    String orderPassword;
    String guestContact;
}
