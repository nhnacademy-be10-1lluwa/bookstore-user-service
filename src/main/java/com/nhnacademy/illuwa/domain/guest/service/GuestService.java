package com.nhnacademy.illuwa.domain.guest.service;

import com.nhnacademy.illuwa.domain.guest.dto.GuestLoginRequest;
import com.nhnacademy.illuwa.domain.guest.dto.GuestResponse;
import com.nhnacademy.illuwa.domain.pointhistory.dto.OrderRequest;

public interface GuestService {
    GuestResponse create(OrderRequest request);
    GuestResponse login(GuestLoginRequest request);
}
