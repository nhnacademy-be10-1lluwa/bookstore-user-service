package com.nhnacademy.illuwa.domain.guest.service.impl;

import com.nhnacademy.illuwa.domain.guest.dto.GuestLoginRequest;
import com.nhnacademy.illuwa.domain.guest.dto.GuestResponse;
import com.nhnacademy.illuwa.domain.guest.entity.Guest;
import com.nhnacademy.illuwa.domain.guest.exception.GuestNotFoundException;
import com.nhnacademy.illuwa.domain.guest.repo.GuestRepository;
import com.nhnacademy.illuwa.domain.guest.service.GuestService;
import com.nhnacademy.illuwa.domain.pointhistory.dto.OrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuestServiceImpl implements GuestService {
    private final GuestRepository guestRepository;

    @Override
    public GuestResponse create(OrderRequest request){
        Guest guest = Guest.builder()
                .orderNumber(request.getOrderNumber())
                .name(request.getGuestName())
                .email(request.getGuestEmail())
                .orderPassword(request.getOrderPassword())
                .contact(request.getGuestContact())
                .build();

        return GuestResponse.from(guestRepository.save(guest));
    }

    @Override
    public GuestResponse login(GuestLoginRequest request) {
        Guest guest = guestRepository.findGuestByOrderNumberAndOrderPassword(request.getOrderNumber(), request.getOrderPassword())
                .orElseThrow(GuestNotFoundException::new);
        return GuestResponse.from(guest);
    }
}
