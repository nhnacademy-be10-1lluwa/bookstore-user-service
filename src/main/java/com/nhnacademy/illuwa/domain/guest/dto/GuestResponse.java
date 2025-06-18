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
    String name;
    String email;
    String contact;
    String orderNumber;

    public static GuestResponse from(Guest guest){
        return GuestResponse.builder()
                .guestId(guest.getGuestId())
                .name(guest.getName())
                .email(guest.getEmail())
                .contact(guest.getContact())
                .orderNumber(guest.getOrderNumber())
                .build();
    }
}
