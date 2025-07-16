package com.nhnacademy.illuwa.domain.guest.dto;

import com.nhnacademy.illuwa.domain.guest.entity.Guest;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class GuestResponse {
    String guestId;
    Long orderId;
    String orderNumber;
    String name;
    String email;
    String contact;

    public static GuestResponse from(Guest guest){
        return GuestResponse.builder()
                .orderId(guest.getOrderId())
                .orderNumber(guest.getOrderNumber())
                .guestId(guest.getGuestId())
                .name(guest.getName())
                .email(guest.getEmail())
                .contact(guest.getContact())
                .build();
    }
}
