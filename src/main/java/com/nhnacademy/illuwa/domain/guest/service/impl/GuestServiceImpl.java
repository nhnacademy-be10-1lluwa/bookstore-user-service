package com.nhnacademy.illuwa.domain.guest.service.impl;

import com.nhnacademy.illuwa.domain.guest.dto.GuestLoginRequest;
import com.nhnacademy.illuwa.domain.guest.dto.GuestOrderRequest;
import com.nhnacademy.illuwa.domain.guest.dto.GuestResponse;
import com.nhnacademy.illuwa.domain.guest.entity.Guest;
import com.nhnacademy.illuwa.domain.guest.exception.GuestNotFoundException;
import com.nhnacademy.illuwa.domain.guest.repo.GuestRepository;
import com.nhnacademy.illuwa.domain.guest.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuestServiceImpl implements GuestService {
    private final GuestRepository guestRepository;

    @Override
    public GuestResponse createGuest(GuestOrderRequest request){
        Guest guest = Guest.builder()
                .guestId(request.getGuestId())
                .orderId(request.getOrderId())
                .orderNumber(request.getOrderNumber())
                .orderPassword(request.getOrderPassword())
                .name(request.getName())
                .email(request.getEmail())
                .contact(request.getContact())
                .build();

        return GuestResponse.from(guestRepository.save(guest));
    }

    @Override
    public GuestResponse getGuest(GuestLoginRequest request) {
        Guest guest = guestRepository.findGuestByOrderNumberAndOrderPassword(request.getOrderNumber(), request.getOrderPassword())
                .orElseThrow(GuestNotFoundException::new);
        return GuestResponse.from(guest);
    }
}
