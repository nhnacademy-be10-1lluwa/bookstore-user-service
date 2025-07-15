package com.nhnacademy.illuwa.domain.guest.service;

import com.nhnacademy.illuwa.domain.guest.dto.GuestLoginRequest;
import com.nhnacademy.illuwa.domain.guest.dto.GuestOrderRequest;
import com.nhnacademy.illuwa.domain.guest.dto.GuestResponse;

public interface GuestService {
    GuestResponse createGuest(GuestOrderRequest request);
    GuestResponse getGuest(GuestLoginRequest request);
}