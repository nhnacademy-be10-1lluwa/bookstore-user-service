package com.nhnacademy.illuwa.domain.point.pointhistory.service.impl;

import com.nhnacademy.illuwa.domain.point.pointhistory.dto.PointHistoryRequest;
import com.nhnacademy.illuwa.domain.point.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.PointHistory;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums.PointHistoryType;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums.PointReason;
import com.nhnacademy.illuwa.domain.point.pointhistory.repo.PointHistoryRepository;
import com.nhnacademy.illuwa.domain.point.pointhistory.utils.PointHistoryMapper;
import com.nhnacademy.illuwa.domain.point.pointhistory.utils.PointHistoryMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointHistoryServiceImplTest {
    PointHistoryMapper pointHistoryMapper = new PointHistoryMapperImpl();

    @Mock
    PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    PointHistoryServiceImpl pointHistoryService;

    @BeforeEach
    void setUp(){
        this.pointHistoryService = new PointHistoryServiceImpl(
                pointHistoryRepository, pointHistoryMapper
        );
    }

    @Test
    @DisplayName("포인트 히스토리 생성")
    void testRecordPointHistory(){
        PointHistoryRequest request = PointHistoryRequest.builder()
                .memberId(1L)
                .amount(BigDecimal.valueOf(777))
                .type(PointHistoryType.EARN)
                .reason(PointReason.GRADE_EVENT)
                .balance(BigDecimal.valueOf(1777))
                .build();

        PointHistory pointHistory = PointHistory.builder()
                .memberId(1L)
                .amount(BigDecimal.valueOf(777))
                .type(PointHistoryType.EARN)
                .reason(PointReason.GRADE_EVENT)
                .balance(BigDecimal.valueOf(1777))
                .build();

        when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(pointHistory);
        PointHistoryResponse result = pointHistoryService.recordPointHistory(request);

        assertEquals(result.getMemberId(), request.getMemberId());
        assertEquals(result.getAmount(), request.getAmount());
        assertEquals(result.getType(), request.getType());
        assertEquals(result.getReason(), request.getReason());
        assertEquals(result.getBalance(), request.getBalance());
    }

    @Test
    @DisplayName("회원 포인트 히스토리 조회 - 전체 리스트")
    void testGetMemberPointHistories() {
        long memberId = 1L;
        PointHistory pointHistory = PointHistory.builder()
                .memberId(memberId)
                .amount(BigDecimal.valueOf(500))
                .type(PointHistoryType.EARN)
                .reason(PointReason.GRADE_EVENT)
                .balance(BigDecimal.valueOf(1500))
                .createdAt(LocalDateTime.now())
                .build();

        when(pointHistoryRepository.findByMemberIdOrderByCreatedAtDesc(memberId))
                .thenReturn(List.of(pointHistory));

        List<PointHistoryResponse> result = pointHistoryService.getMemberPointHistories(memberId);

        assertEquals(1, result.size());
        assertEquals(memberId, result.get(0).getMemberId());
        assertEquals(pointHistory.getAmount(), result.get(0).getAmount());
        assertEquals(pointHistory.getType(), result.get(0).getType());
        assertEquals(pointHistory.getReason(), result.get(0).getReason());
        assertEquals(pointHistory.getBalance(), result.get(0).getBalance());
    }

    @Test
    @DisplayName("회원 포인트 히스토리 페이징 조회 - EARN 타입")
    void testGetPagedMemberPointHistories_Earn() {
        long memberId = 1L;
        Pageable pageable = Pageable.ofSize(10);
        PointHistory pointHistory = PointHistory.builder()
                .memberId(memberId)
                .amount(BigDecimal.valueOf(1000))
                .type(PointHistoryType.EARN)
                .reason(PointReason.JOIN)
                .balance(BigDecimal.valueOf(2000))
                .createdAt(LocalDateTime.now())
                .build();

        Page<PointHistory> page = new org.springframework.data.domain.PageImpl<>(List.of(pointHistory));

        when(pointHistoryRepository.findByMemberIdAndTypeOrderByCreatedAtDesc(
                memberId, PointHistoryType.EARN, pageable)).thenReturn(page);

        Page<PointHistoryResponse> result = pointHistoryService.getPagedMemberPointHistories(memberId, "EARN", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(memberId, result.getContent().get(0).getMemberId());
    }

    @Test
    @DisplayName("회원 포인트 히스토리 페이징 조회 - USE 타입")
    void testGetPagedMemberPointHistories_Use() {
        long memberId = 1L;
        Pageable pageable = Pageable.ofSize(10);
        PointHistory pointHistory = PointHistory.builder()
                .memberId(memberId)
                .amount(BigDecimal.valueOf(300))
                .type(PointHistoryType.DEDUCT)
                .reason(PointReason.USED_IN_ORDER)
                .balance(BigDecimal.valueOf(1200))
                .createdAt(LocalDateTime.now())
                .build();

        Page<PointHistory> page = new org.springframework.data.domain.PageImpl<>(List.of(pointHistory));

        when(pointHistoryRepository.findByMemberIdAndTypeOrderByCreatedAtDesc(
                memberId, PointHistoryType.DEDUCT, pageable)).thenReturn(page);

        Page<PointHistoryResponse> result = pointHistoryService.getPagedMemberPointHistories(memberId, "USE", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(memberId, result.getContent().get(0).getMemberId());
    }

    @Test
    @DisplayName("회원 포인트 히스토리 페이징 조회 - 전체 조회")
    void testGetPagedMemberPointHistories_All() {
        long memberId = 1L;
        Pageable pageable = Pageable.ofSize(10);
        PointHistory pointHistory = PointHistory.builder()
                .memberId(memberId)
                .amount(BigDecimal.valueOf(100))
                .type(PointHistoryType.EARN)
                .reason(PointReason.JOIN)
                .balance(BigDecimal.valueOf(1100))
                .createdAt(LocalDateTime.now())
                .build();

        Page<PointHistory> page = new org.springframework.data.domain.PageImpl<>(List.of(pointHistory));

        when(pointHistoryRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId, pageable)).thenReturn(page);

        Page<PointHistoryResponse> result = pointHistoryService.getPagedMemberPointHistories(memberId, "ANYTHING_ELSE", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(memberId, result.getContent().get(0).getMemberId());
    }

}