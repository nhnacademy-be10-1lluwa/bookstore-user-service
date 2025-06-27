package com.nhnacademy.illuwa.domain.guest.service;

import com.nhnacademy.illuwa.domain.guest.dto.GuestLoginRequest;
import com.nhnacademy.illuwa.domain.guest.dto.GuestOrderRequest;
import com.nhnacademy.illuwa.domain.guest.dto.GuestResponse;

public interface GuestService {
    GuestResponse create(GuestOrderRequest request);
    GuestResponse login(GuestLoginRequest request);
}
