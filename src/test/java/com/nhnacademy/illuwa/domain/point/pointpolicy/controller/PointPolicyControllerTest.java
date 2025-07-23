package com.nhnacademy.illuwa.domain.point.pointpolicy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.point.pointpolicy.dto.PointPolicyCreateRequest;
import com.nhnacademy.illuwa.domain.point.pointpolicy.dto.PointPolicyResponse;
import com.nhnacademy.illuwa.domain.point.pointpolicy.dto.PointPolicyUpdateRequest;
import com.nhnacademy.illuwa.domain.point.pointpolicy.entity.enums.PointValueType;
import com.nhnacademy.illuwa.domain.point.pointpolicy.entity.enums.PolicyStatus;
import com.nhnacademy.illuwa.domain.point.pointpolicy.service.PointPolicyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PointPolicyController.class)
class PointPolicyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PointPolicyService pointPolicyService;

    @Autowired
    ObjectMapper objectMapper;

    private PointPolicyResponse createResponse() {
        return PointPolicyResponse.builder()
                .policyKey("EVENT_POLICY")
                .value(BigDecimal.valueOf(0.05))
                .valueType(PointValueType.RATE)
                .description("이벤트 포인트 적립률")
                .status(PolicyStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("정책 생성")
    void testCreatePolicy() throws Exception {
        PointPolicyCreateRequest request = PointPolicyCreateRequest.builder()
                .policyKey("EVENT_POLICY")
                .value(BigDecimal.valueOf(0.05))
                .valueType(PointValueType.RATE)
                .description("이벤트 포인트 적립률")
                .build();

        PointPolicyResponse response = createResponse();

        when(pointPolicyService.createPointPolicy(any())).thenReturn(response);

        mockMvc.perform(post("/api/admin/point-policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.policyKey").value("EVENT_POLICY"))
                .andExpect(jsonPath("$.value").value(0.05))
                .andExpect(jsonPath("$.valueType").value("RATE"))
                .andExpect(jsonPath("$.description").value("이벤트 포인트 적립률"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("정책 목록 조회")
    void testGetPolicyList() throws Exception {
        when(pointPolicyService.findAllPointPolicy()).thenReturn(List.of(createResponse()));

        mockMvc.perform(get("/api/admin/point-policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].policyKey").value("EVENT_POLICY"));
    }

    @Test
    @DisplayName("정책 단건 조회")
    void testGetPolicy() throws Exception {
        when(pointPolicyService.findByPolicyKey("EVENT_POLICY")).thenReturn(createResponse());

        mockMvc.perform(get("/api/admin/point-policies/EVENT_POLICY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policyKey").value("EVENT_POLICY"));
    }

    @Test
    @DisplayName("정책 수정")
    void testUpdatePolicy() throws Exception {
        PointPolicyUpdateRequest request = PointPolicyUpdateRequest.builder()
                .value(BigDecimal.valueOf(0.03))
                .valueType(PointValueType.RATE)
                .description("수정된 설명")
                .status(PolicyStatus.ACTIVE)
                .build();

        PointPolicyResponse response = createResponse();
        response.setValue(BigDecimal.valueOf(0.03));
        response.setDescription("수정된 설명");

        when(pointPolicyService.updatePointPolicy(eq("EVENT_POLICY"), any())).thenReturn(response);

        mockMvc.perform(put("/api/admin/point-policies/EVENT_POLICY")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(0.03))
                .andExpect(jsonPath("$.description").value("수정된 설명"));
    }

    @Test
    @DisplayName("정책 삭제")
    void testDeletePolicy() throws Exception {
        doNothing().when(pointPolicyService).deletePointPolicy("EVENT_POLICY");

        mockMvc.perform(delete("/api/admin/point-policies/EVENT_POLICY"))
                .andExpect(status().isOk());
    }
}
