package com.nhnacademy.illuwa.domain.point.pointhistory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.point.pointhistory.dto.PointAfterOrderRequest;
import com.nhnacademy.illuwa.domain.point.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.point.pointhistory.dto.UsedPointRequest;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums.PointHistoryType;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums.PointReason;
import com.nhnacademy.illuwa.domain.point.utils.PointManager;
import com.nhnacademy.illuwa.domain.point.pointhistory.service.PointHistoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PointHistoryController.class)
class PointHistoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PointManager pointManager;

    @MockBean
    PointHistoryService pointHistoryService;

    @Autowired
    ObjectMapper objectMapper;

    private PointHistoryResponse createResponse() {
        return PointHistoryResponse.builder()
                .memberId(1L)
                .amount(BigDecimal.valueOf(777))
                .type(PointHistoryType.EARN)
                .reason(PointReason.GRADE_EVENT)
                .balance(BigDecimal.valueOf(1777))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("회원 현재 포인트 조회")
    void testGetMemberPoint() throws Exception {
        when(pointManager.getMemberPoint(1L)).thenReturn(BigDecimal.valueOf(1000));

        mockMvc.perform(get("/api/members/points")
                        .header("X-USER-ID", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("1000"));
    }

    @Test
    @DisplayName("회원 포인트 히스토리 전체 조회")
    void testGetMemberPointHistories() throws Exception {
        PointHistoryResponse response = createResponse();

        when(pointHistoryService.getMemberPointHistories(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/members/points/histories")
                        .header("X-USER-ID", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberId").value(1L));
    }

    @Test
    @DisplayName("회원 포인트 히스토리 페이징 조회")
    void testGetPagedMemberPointHistories() throws Exception {
        PointHistoryResponse response = createResponse();

        when(pointHistoryService.getPagedMemberPointHistories(eq(1L), eq("ALL"), any()))
                .thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 6), 1));

        mockMvc.perform(get("/api/members/points/histories/paged")
                        .header("X-USER-ID", 1L)
                        .param("type", "ALL")
                        .param("page", "0")
                        .param("size", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].memberId").value(1L));
    }

    @Test
    @DisplayName("이벤트 포인트 지급")
    void testEarnEventPoint() throws Exception {
        PointHistoryResponse response = createResponse();

        when(pointManager.processEventPoint(eq(1L), eq(PointReason.GRADE_EVENT), isNull()))
                .thenReturn(Optional.of(response));

        mockMvc.perform(post("/api/members/points/event")
                        .header("X-USER-ID", 1L)
                        .param("reason", "GRADE_EVENT"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value(1L))
                .andExpect(jsonPath("$.type").value("EARN"))
                .andExpect(jsonPath("$.reason").value("GRADE_EVENT"))
                .andExpect(jsonPath("$.amount").value(777))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("주문 후 포인트 적립")
    void testEarnPointAfterOrder() throws Exception {
        PointAfterOrderRequest request = PointAfterOrderRequest.builder()
                .memberId(1L)
                .price(BigDecimal.valueOf(500))
                .build();

        PointHistoryResponse response = createResponse();
        response.setReason(PointReason.PURCHASE);
        response.setAmount(BigDecimal.valueOf(200));  //정책에 의해 계산된 금액이라고 가정

        when(pointManager.processOrderPoint(any())).thenReturn(Optional.of(response));

        mockMvc.perform(post("/api/members/points/order/earn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value(1L))
                .andExpect(jsonPath("$.type").value("EARN"))
                .andExpect(jsonPath("$.reason").value("PURCHASE"))
                .andExpect(jsonPath("$.amount").value(BigDecimal.valueOf(200)))
                .andExpect(jsonPath("$.createdAt").exists());
    }


    @Test
    @DisplayName("주문에 의한 포인트 차감")
    void testDeductPointInOrder() throws Exception {
        UsedPointRequest request = UsedPointRequest.builder()
                .memberId(1L)
                .usedPoint(BigDecimal.valueOf(300))
                .build();
        PointHistoryResponse response = createResponse();
        response.setType(PointHistoryType.DEDUCT);
        response.setReason(PointReason.USED_IN_ORDER);
        response.setAmount(BigDecimal.valueOf(300));

        when(pointManager.processUsedPoint(any())).thenReturn(response);

        mockMvc.perform(post("/api/members/points/order/use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value(1L))
                .andExpect(jsonPath("$.type").value("DEDUCT"))
                .andExpect(jsonPath("$.reason").value("USED_IN_ORDER"))
                .andExpect(jsonPath("$.amount").value(300))
                .andExpect(jsonPath("$.createdAt").exists());

    }

    @Test
    @DisplayName("주문 취소/반품으로 포인트 재적립")
    void testEarnPointAfterRefund() throws Exception {
        PointAfterOrderRequest request = PointAfterOrderRequest.builder()
                .memberId(1L)
                .price(BigDecimal.valueOf(3000))
                .build();

        PointHistoryResponse response = createResponse();
        response.setReason(PointReason.REFUND);
        response.setAmount(BigDecimal.valueOf(3000));

        when(pointManager.processRefundPoint(any())).thenReturn(response);

        mockMvc.perform(post("/api/members/points/order/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value(1L))
                .andExpect(jsonPath("$.type").value("EARN"))
                .andExpect(jsonPath("$.reason").value("REFUND"))
                .andExpect(jsonPath("$.amount").value(3000))
                .andExpect(jsonPath("$.createdAt").exists());

    }
}
