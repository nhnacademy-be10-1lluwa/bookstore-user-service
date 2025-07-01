package com.nhnacademy.illuwa.domain.guest.dto;

import com.nhnacademy.illuwa.domain.guest.entity.Guest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestResponse {
    long guestId;
    long orderId;
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
