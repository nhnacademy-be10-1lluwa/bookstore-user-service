package com.nhnacademy.illuwa.domain.pointpolicy.service.impl;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    List<PointPolicyCreateRequest> requestList = List.of(
            new PointPolicyCreateRequest("join_point", new BigDecimal("5000"), PointValueType.AMOUNT, "회원가입 포인트 적립액"),
            new PointPolicyCreateRequest("review_point", new BigDecimal("200"), PointValueType.AMOUNT, "리뷰 포인트 적립액"),
            new PointPolicyCreateRequest("photo_review_point", new BigDecimal("500"), PointValueType.AMOUNT, "포토리뷰 포인트 적립액"),
            new PointPolicyCreateRequest("book_default_rate", new BigDecimal("1.00"), PointValueType.RATE, "도서구매 기본 적립률")
    );

    @BeforeEach
    void SetUp(){
        pointPolicyService = new PointPolicyServiceImpl(pointPolicyRepository, pointPolicyMapper);
    }

    @Test
    @DisplayName("포인트 정책 전체등록")
    void testSaveAllPointPolicy() {
        List<PointPolicy> entityList = requestList.stream()
                .map(req -> new PointPolicy(req.getPolicyKey(), req.getValue(), req.getValueType(), req.getDescription()))
                .toList();

        when(pointPolicyRepository.saveAll(any())).thenReturn(entityList);

        List<PointPolicyResponse> saved = pointPolicyService.saveAllPointPolicy(requestList);
        assertEquals(4, saved.size());

        verify(pointPolicyRepository).saveAll(any());
    }


    @Test
    @DisplayName("포인트 정책 전체등록 - 중복된 policyKey 예외 발생")
    void testSaveAllPointPolicy_DuplicateKey() {
        List<PointPolicyCreateRequest> duplicateList = List.of(
                new PointPolicyCreateRequest("join_point", new BigDecimal("1000"), PointValueType.AMOUNT, "설명1"),
                new PointPolicyCreateRequest("join_point", new BigDecimal("2000"), PointValueType.AMOUNT, "설명2")
        );

        DuplicatePointPolicyException ex = Assertions.assertThrows(DuplicatePointPolicyException.class,
                () -> pointPolicyService.saveAllPointPolicy(duplicateList));

        Assertions.assertTrue(ex.getMessage().contains("이미 존재하는 포인트 정책입니다"));
    }


    @Test
    @DisplayName("포인트 정책 단일조회")
    void testFindByPolicyKey() {
        PointPolicyCreateRequest joinPointRequestDto = requestList.getFirst();
        PointPolicy entity = new PointPolicy(
                joinPointRequestDto.getPolicyKey(),
                joinPointRequestDto.getValue(),
                joinPointRequestDto.getValueType(),
                joinPointRequestDto.getDescription()
        );

        when(pointPolicyRepository.findById("join_point")).thenReturn(Optional.of(entity));

        PointPolicyResponse response = pointPolicyService.findByPolicyKey("join_point");

        Assertions.assertEquals("join_point", response.getPolicyKey());
        Assertions.assertEquals(new BigDecimal("5000"), response.getValue());
        Assertions.assertEquals(PointValueType.AMOUNT, response.getValueType());
        Assertions.assertEquals("회원가입 포인트 적립액", response.getDescription());

        verify(pointPolicyRepository).findById("join_point");
    }

    @Test
    @DisplayName("포인트 정책 단일조회 - 존재하지 않는 policyKey 예외")
    void testFindByPolicyKey_NotFound() {
        when(pointPolicyRepository.findById("unknown")).thenReturn(Optional.empty());

        Assertions.assertThrows(PointPolicyNotFoundException.class,
                () -> pointPolicyService.findByPolicyKey("unknown"));

        verify(pointPolicyRepository).findById("unknown");
    }

    @Test
    @DisplayName("포인트 정책 전체조회")
    void testFindAllPointPolicy() {
        List<PointPolicy> entities = requestList.stream()
                .map(r -> new PointPolicy(r.getPolicyKey(), r.getValue(), r.getValueType(), r.getDescription()))
                .toList();

        when(pointPolicyRepository.findAll()).thenReturn(entities);

        List<PointPolicyResponse> responses = pointPolicyService.findAllPointPolicy();
        assertEquals(4, responses.size());

        verify(pointPolicyRepository).findAll();
    }

    @Test
    @DisplayName("포인트 정책 전체조회 - 빈 리스트 반환")
    void testFindAllPointPolicy_Empty() {
        when(pointPolicyRepository.findAll()).thenReturn(List.of());

        List<PointPolicyResponse> responses = pointPolicyService.findAllPointPolicy();
        Assertions.assertTrue(responses.isEmpty());

        verify(pointPolicyRepository).findAll();
    }

    @Test
    @DisplayName("포인트 정책 수정")
    void testUpdatePointPolicy() {
        PointPolicy original = new PointPolicy("review_point", new BigDecimal("200"), PointValueType.AMOUNT, "리뷰 포인트 적립액");

        PointPolicyUpdateRequest request = new PointPolicyUpdateRequest();
        request.setValue(new BigDecimal("0.30"));
        request.setValueType(PointValueType.RATE);
        request.setDescription("리뷰 포인트 적립률");

        PointPolicy updated = new PointPolicy("review_point", request.getValue(), request.getValueType(), request.getDescription());
        PointPolicyResponse updatedDto = new PointPolicyResponse("review_point", request.getValue(), request.getValueType(), request.getDescription());

        when(pointPolicyRepository.findById("review_point")).thenReturn(Optional.of(original));

        PointPolicyResponse result = pointPolicyService.updatePointPolicy("review_point", request);

        assertEquals(new BigDecimal("0.30"), result.getValue());
        assertEquals(PointValueType.RATE, result.getValueType());
        assertEquals("리뷰 포인트 적립률", result.getDescription());

        verify(pointPolicyRepository).findById("review_point");
    }

    @Test
    @DisplayName("포인트 정책 수정 - 존재하지 않는 policyKey 예외")
    void testUpdatePointPolicy_NotFound() {
        PointPolicyUpdateRequest request = new PointPolicyUpdateRequest();
        when(pointPolicyRepository.findById("unknown")).thenReturn(Optional.empty());

        Assertions.assertThrows(PointPolicyNotFoundException.class,
                () -> pointPolicyService.updatePointPolicy("unknown", request));

        verify(pointPolicyRepository).findById("unknown");
    }
}
