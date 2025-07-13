package com.nhnacademy.illuwa.domain.guest.service.impl;

import com.nhnacademy.illuwa.domain.guest.dto.GuestLoginRequest;
import com.nhnacademy.illuwa.domain.guest.dto.GuestOrderRequest;
import com.nhnacademy.illuwa.domain.guest.dto.GuestResponse;
import com.nhnacademy.illuwa.domain.guest.entity.Guest;
import com.nhnacademy.illuwa.domain.guest.repo.GuestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class GuestServiceImplTest {

    @Mock
    GuestRepository guestRepository;

    @InjectMocks
    GuestServiceImpl guestService;

    GuestOrderRequest guestOrderRequest;
    Guest testGuest;


    @BeforeEach
    void Setup(){
        guestOrderRequest = GuestOrderRequest.builder()
                .guestId("123456789101112131415161718")
                .orderId(1L)
                .orderNumber("20250630032809-123456")
                .orderPassword("guest!123!")
                .name("비회원")
                .email("guest@naver.com")
                .contact("010-1234-5678")
                .build();

        testGuest = Guest.builder()
                .guestId(guestOrderRequest.getGuestId())
                .orderId(guestOrderRequest.getOrderId())
                .orderNumber(guestOrderRequest.getOrderNumber())
                .orderPassword(guestOrderRequest.getOrderPassword())
                .name(guestOrderRequest.getName())
                .email(guestOrderRequest.getEmail())
                .contact(guestOrderRequest.getContact())
                .build();
    }

    @Test
    @DisplayName("비회원 정보생성")
    void testCreateGuest(){
        when(guestRepository.save(any(Guest.class))).thenReturn(testGuest);

        GuestResponse response = guestService.createGuest(guestOrderRequest);
        assertThat(response.getGuestId()).isEqualTo(guestOrderRequest.getGuestId());
    }

    @Test
    @DisplayName("비회원 로그인")
    void testLogin(){
        when(guestRepository.findGuestByOrderNumberAndOrderPassword(anyString(), anyString())).thenReturn(Optional.of(testGuest));

        GuestLoginRequest loginRequest = new GuestLoginRequest(guestOrderRequest.getOrderNumber(), guestOrderRequest.getOrderPassword());
        GuestResponse response = guestService.getGuest(loginRequest);

        assertThat(response.getOrderId()).isEqualTo(testGuest.getOrderId());
        assertThat(response.getOrderNumber()).isEqualTo(testGuest.getOrderNumber());
        assertThat(response.getName()).isEqualTo(testGuest.getName());
    }
}