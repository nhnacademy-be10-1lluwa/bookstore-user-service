package com.nhnacademy.illuwa.domain.memberaddress.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressRequest;
import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressResponse;
import com.nhnacademy.illuwa.domain.memberaddress.service.MemberAddressService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.BDDMockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberAddressController.class)
class MemberAddressControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MemberAddressService memberAddressService;

    @Autowired
    ObjectMapper objectMapper;

    MemberAddressRequest createRequest(boolean isDefault) {
        return MemberAddressRequest.builder()
                .postCode("12345")
                .address("서울시 강남구")
                .detailAddress("101호")
                .addressName("집")
                .recipientName("공주님")
                .recipientContact("010-1234-5678")
                .defaultAddress(isDefault)
                .build();
    }

    MemberAddressResponse createResponse(long id, boolean isDefault) {
        return MemberAddressResponse.builder()
                .memberAddressId(id)
                .postCode("12345")
                .address("서울시 강남구")
                .detailAddress("101호")
                .addressName("집")
                .recipientName("공주님")
                .recipientContact("010-1234-5678")
                .defaultAddress(isDefault)
                .build();
    }

    @Test
    @DisplayName("회원 주소 목록 조회")
    void getAddressList() throws Exception {
        long memberId = 1L;
        given(memberAddressService.getMemberAddressList(memberId))
                .willReturn(List.of(createResponse(1L, true)));

        mockMvc.perform(get("/members/address", memberId)
                .header("X-USER_ID", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberAddressId").value(1L))
                .andExpect(jsonPath("$[0].recipientName").value("공주님"));
    }

    @Test
    @DisplayName("회원 주소 단건 조회")
    void getAddress() throws Exception {
        long addressId = 1L;
        given(memberAddressService.getMemberAddress(addressId))
                .willReturn(createResponse(addressId, true));

        mockMvc.perform(get("/members/address/{addressId}", addressId)
                        .header("X-USER_ID", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberAddressId").value(addressId))
                .andExpect(jsonPath("$.recipientName").value("공주님"));
    }

    @Test
    @DisplayName("회원 주소 등록")
    void createAddress() throws Exception {
        long memberId = 1L;
        MemberAddressRequest request = createRequest(true);
        MemberAddressResponse response = createResponse(100L, true);

        given(memberAddressService.registerMemberAddress(eq(memberId), any()))
                .willReturn(response);

        mockMvc.perform(post("/members/address", memberId)
                        .header("X-USER_ID", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberAddressId").value(100L))
                .andExpect(jsonPath("$.defaultAddress").value(true));
    }

    @Test
    @DisplayName("회원 주소 수정")
    void updateAddress() throws Exception {
        long addressId = 1L;
        MemberAddressRequest request = createRequest(false);
        MemberAddressResponse response = createResponse(addressId, false);

        given(memberAddressService.updateMemberAddress(eq(addressId), any()))
                .willReturn(response);

        mockMvc.perform(patch("/members/address/{addressId}", addressId)
                        .header("X-USER_ID", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberAddressId").value(addressId));
    }

    @Test
    @DisplayName("회원 주소 삭제")
    void deleteAddress() throws Exception {
        long addressId = 1L;

        willDoNothing().given(memberAddressService).deleteMemberAddress(addressId);

        mockMvc.perform(delete("/members/address/{addressId}", addressId)
                .header("X-USER_ID", 1L))
                .andExpect(status().isOk());
    }
}