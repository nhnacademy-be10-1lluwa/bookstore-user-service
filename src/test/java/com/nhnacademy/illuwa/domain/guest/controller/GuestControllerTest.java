package com.nhnacademy.illuwa.domain.guest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.guest.dto.GuestLoginRequest;
import com.nhnacademy.illuwa.domain.guest.dto.GuestOrderRequest;
import com.nhnacademy.illuwa.domain.guest.dto.GuestResponse;
import com.nhnacademy.illuwa.domain.guest.service.GuestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GuestController.class)
class GuestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    GuestService guestService;

    @Autowired
    ObjectMapper objectMapper;

    GuestResponse dummyResponse = GuestResponse.builder()
            .guestId("123456789101112131415161718")
            .orderId(1L)
            .orderNumber("20250630032809-123456")
            .name("비회원")
            .email("guest@naver.com")
            .contact("010-1234-5678")
            .build();

    @Test
    @DisplayName("게스트 정보조회 성공")
    void testGetGuest() throws Exception {
        GuestLoginRequest loginRequest = new GuestLoginRequest("20250630032809-123456", "guestPw!123");

        Mockito.when(guestService.getGuest(any(GuestLoginRequest.class)))
                .thenReturn(dummyResponse);

        mockMvc.perform(post("/api/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.orderNumber").value("20250630032809-123456"))
                .andExpect(jsonPath("$.name").value("비회원"))
                .andExpect(jsonPath("$.email").value("guest@naver.com"))
                .andExpect(jsonPath("$.contact").value("010-1234-5678"));
    }

    @Test
    @DisplayName("비회원 정보 생성 성공")
    void testCreateGuest() throws Exception {
        GuestOrderRequest orderRequest = GuestOrderRequest.builder()
                .guestId("123456789101112131415161718")
                .name("비회원")
                .email("guest@naver.com")
                .contact("010-1234-5678")
                .orderId(1L)
                .orderNumber("20250630032809-123456")
                .orderPassword("guestPw!123")
                .build();

        Mockito.when(guestService.createGuest(any(GuestOrderRequest.class)))
                .thenReturn(dummyResponse);

        mockMvc.perform(post("/api/guests/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.guestId").value("123456789101112131415161718"))
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.orderNumber").value("20250630032809-123456"))
                .andExpect(jsonPath("$.name").value("비회원"))
                .andExpect(jsonPath("$.email").value("guest@naver.com"))
                .andExpect(jsonPath("$.contact").value("010-1234-5678"));
    }
}