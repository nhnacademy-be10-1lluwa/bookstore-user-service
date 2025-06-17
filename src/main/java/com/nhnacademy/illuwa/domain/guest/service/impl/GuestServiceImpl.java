package com.nhnacademy.illuwa.domain.guest.service.impl;

import com.nhnacademy.illuwa.domain.guest.dto.GuestLoginRequest;
import com.nhnacademy.illuwa.domain.guest.dto.GuestResponse;
import com.nhnacademy.illuwa.domain.guest.entity.Guest;
import com.nhnacademy.illuwa.domain.guest.repo.GuestRepository;
import com.nhnacademy.illuwa.domain.guest.service.GuestService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GuestServiceImpl implements GuestService {
    private final GuestRepository guestRepository;

    @Override
    public GuestResponse login(GuestLoginRequest request) {
        Guest guest = guestRepository.findGuestByOrderIdAndOrderPassword(request.getOrderId(), request.getOrderPassword());
        /*if(guest == null){
            throw new GuestNotFoundException(guest.getGuestId());
        }*/
        return GuestResponse.from(guest);
    }
}
