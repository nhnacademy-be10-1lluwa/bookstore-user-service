package com.nhnacademy.illuwa.domain.pointpolicy.service.impl;

import com.nhnacademy.illuwa.common.exception.InvalidInputException;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyCreateRequest;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyResponse;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyUpdateRequest;
import com.nhnacademy.illuwa.domain.pointpolicy.entity.PointPolicy;
import com.nhnacademy.illuwa.domain.pointpolicy.entity.enums.PointValueType;
import com.nhnacademy.illuwa.domain.pointpolicy.exception.DuplicatePointPolicyException;
import com.nhnacademy.illuwa.domain.pointpolicy.exception.PointPolicyNotFoundException;
import com.nhnacademy.illuwa.domain.pointpolicy.repo.PointPolicyRepository;
import com.nhnacademy.illuwa.domain.pointpolicy.utils.PointPolicyMapper;
import com.nhnacademy.illuwa.domain.pointpolicy.utils.PointPolicyMapperImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointPolicyServiceImplTest {

    @Mock
    private PointPolicyRepository pointPolicyRepository;

    PointPolicyMapper pointPolicyMapper = new PointPolicyMapperImpl();

    @InjectMocks
    private PointPolicyServiceImpl pointPolicyService;

    PointPolicyCreateRequest request;
    PointPolicy testPolicy;

    @BeforeEach
    void SetUp(){
        pointPolicyService = new PointPolicyServiceImpl(pointPolicyRepository, pointPolicyMapper);

        request = PointPolicyCreateRequest.builder()
                .policyKey("thanks_point")
                .value(new BigDecimal("1000"))
                .valueType(PointValueType.AMOUNT)
                .description("설명1")
                .build();

        testPolicy = PointPolicy.builder()
                .policyKey(request.getPolicyKey())
                .value(request.getValue())
                .valueType(request.getValueType())
                .description(request.getDescription())
                .build();
    }

    @Test
    @DisplayName("포인트 정책 등록")
    void testCreatePointPolicy() {
        when(pointPolicyRepository.save(any(PointPolicy.class))).thenReturn(testPolicy);
        PointPolicyResponse saved = pointPolicyService.createPointPolicy(request);

        assertNotNull(saved);
        assertEquals(request.getPolicyKey(), saved.getPolicyKey());
        assertEquals(request.getValue(), saved.getValue());
        assertEquals(request.getValueType(), saved.getValueType());
        assertEquals(request.getDescription(), saved.getDescription());
    }

    @Test
    @DisplayName("포인트 정책 등록 - 중복된 policyKey 예외 발생")
    void testCreatePointPolicy_DuplicateKey() {
        PointPolicyCreateRequest request = new PointPolicyCreateRequest("duplicate", new BigDecimal("1000"), PointValueType.AMOUNT, "설명1");

        PointPolicy existingPolicy = PointPolicy.builder()
                .policyKey("duplicate")
                .value(new BigDecimal("1000"))
                .valueType(PointValueType.AMOUNT)
                .description("설명1")
                .build();

        when(pointPolicyRepository.findAll())
                .thenReturn(List.of()) // 첫 호출: 중복 없음
                .thenReturn(List.of(existingPolicy)); // 두 번째 호출: 중복 발견

        when(pointPolicyRepository.save(any(PointPolicy.class)))
                .thenReturn(existingPolicy);

        pointPolicyService.createPointPolicy(request);

        //두번째 시도
        DuplicatePointPolicyException ex = assertThrows(DuplicatePointPolicyException.class,
                () -> pointPolicyService.createPointPolicy(request));

        Assertions.assertTrue(ex.getMessage().contains("이미 존재하는 포인트 정책입니다: duplicate"));
    }

    @Test
    @DisplayName("포인트 정책 단일 조회")
    void testFindByPolicyKey() {
        when(pointPolicyRepository.findById("thanks_point")).thenReturn(Optional.of(testPolicy));

        PointPolicyResponse response = pointPolicyService.findByPolicyKey("thanks_point");

        assertNotNull(response);
        assertEquals("thanks_point", response.getPolicyKey());
        verify(pointPolicyRepository).findById("thanks_point");
    }

    @Test
    @DisplayName("포인트 정책 단일 조회 - 존재하지 않을 경우 예외")
    void testFindByPolicyKey_NotFound() {
        when(pointPolicyRepository.findById("missing_key")).thenReturn(Optional.empty());

        assertThrows(PointPolicyNotFoundException.class,
                () -> pointPolicyService.findByPolicyKey("missing_key"));
    }

    @Test
    @DisplayName("모든 포인트 정책 조회")
    void testFindAllPointPolicy() {
        List<PointPolicy> list = List.of(testPolicy);
        when(pointPolicyRepository.findAll()).thenReturn(list);

        List<PointPolicyResponse> responseList = pointPolicyService.findAllPointPolicy();

        assertEquals(1, responseList.size());
        assertEquals("thanks_point", responseList.getFirst().getPolicyKey());
    }

    @Test
    @DisplayName("포인트 정책 수정 - 성공")
    void testUpdatePointPolicy() {
        PointPolicyUpdateRequest updateRequest = PointPolicyUpdateRequest.builder()
                .value(new BigDecimal("0.3"))
                .valueType(PointValueType.RATE)
                .description("적립율 수정")
                .build();

        when(pointPolicyRepository.findById("thanks_point")).thenReturn(Optional.of(testPolicy));
        when(pointPolicyRepository.save(any(PointPolicy.class))).thenReturn(testPolicy);

        PointPolicyResponse updated = pointPolicyService.updatePointPolicy("thanks_point", updateRequest);

        assertNotNull(updated);
        assertEquals("thanks_point", updated.getPolicyKey());
        verify(pointPolicyRepository).save(any(PointPolicy.class));
    }

    @Test
    @DisplayName("포인트 정책 수정 - 존재하지 않을 경우 예외")
    void testUpdatePointPolicy_NotFound() {
        when(pointPolicyRepository.findById("invalid")).thenReturn(Optional.empty());

        PointPolicyUpdateRequest request = PointPolicyUpdateRequest.builder()
                .value(BigDecimal.ONE)
                .valueType(PointValueType.RATE)
                .description("desc")
                .build();

        assertThrows(PointPolicyNotFoundException.class,
                () -> pointPolicyService.updatePointPolicy("invalid", request));
    }

    @Test
    @DisplayName("포인트 정책 수정 - 잘못된 RATE 값 예외")
    void testUpdatePointPolicy_InvalidRateValue() {
        PointPolicyUpdateRequest invalidRequest = PointPolicyUpdateRequest.builder()
                .value(new BigDecimal("1.5")) // > 1.0
                .valueType(PointValueType.RATE)
                .description("비정상 비율")
                .build();

        when(pointPolicyRepository.findById("thanks_point")).thenReturn(Optional.of(testPolicy));

        assertThrows(InvalidInputException.class,
                () -> pointPolicyService.updatePointPolicy("thanks_point", invalidRequest));
    }

    @Test
    @DisplayName("포인트 정책 삭제 - 성공")
    void testDeletePointPolicy() {
        when(pointPolicyRepository.findById("thanks_point")).thenReturn(Optional.of(testPolicy));

        pointPolicyService.deletePointPolicy("thanks_point");

        verify(pointPolicyRepository).deleteById("thanks_point");
    }

    @Test
    @DisplayName("포인트 정책 삭제 - 존재하지 않을 경우 예외")
    void testDeletePointPolicy_NotFound() {
        when(pointPolicyRepository.findById("invalid")).thenReturn(Optional.empty());

        assertThrows(PointPolicyNotFoundException.class,
                () -> pointPolicyService.deletePointPolicy("invalid"));
    }
}
