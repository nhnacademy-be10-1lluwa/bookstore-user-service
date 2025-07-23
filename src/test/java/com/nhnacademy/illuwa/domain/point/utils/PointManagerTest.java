package com.nhnacademy.illuwa.domain.point.utils;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.exception.InactiveMemberException;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import com.nhnacademy.illuwa.domain.point.exception.InvalidPointOperationException;
import com.nhnacademy.illuwa.domain.point.pointhistory.dto.*;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums.PointReason;
import com.nhnacademy.illuwa.domain.point.pointhistory.service.PointHistoryService;
import com.nhnacademy.illuwa.domain.point.pointpolicy.dto.PointPolicyResponse;
import com.nhnacademy.illuwa.domain.point.pointpolicy.entity.enums.PointValueType;
import com.nhnacademy.illuwa.domain.point.pointpolicy.entity.enums.PolicyStatus;
import com.nhnacademy.illuwa.domain.point.pointpolicy.service.PointPolicyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointManagerTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PointPolicyService pointPolicyService;

    @Mock
    PointHistoryService pointHistoryService;

    @InjectMocks
    PointManager pointManager;

    @Test
    @DisplayName("회원 포인트 조회 - 성공")
    void getMemberPoint_success() {
        long memberId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mock(Member.class)));
        when(memberRepository.findPoint(memberId)).thenReturn(BigDecimal.TEN);

        BigDecimal point = pointManager.getMemberPoint(memberId);

        assertThat(point).isEqualTo(BigDecimal.TEN);
    }

    @Test
    @DisplayName("회원 포인트 조회 - 실패")
    void getMemberPoint_fail_notFound() {
        long memberId = 2L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pointManager.getMemberPoint(memberId))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("사용한 포인트 처리 - 성공")
    void processUsedPoint_success() {
        UsedPointRequest request = UsedPointRequest.builder()
                .memberId(1L)
                .usedPoint(BigDecimal.valueOf(500))
                .build();

        Member member = mock(Member.class);
        when(member.getPoint()).thenReturn(BigDecimal.valueOf(1000));

        when(memberRepository.findPoint(1L)).thenReturn(BigDecimal.valueOf(1000));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        when(pointHistoryService.recordPointHistory(any())).thenReturn(mock(PointHistoryResponse.class));

        PointHistoryResponse response = pointManager.processUsedPoint(request);

        assertThat(response).isNotNull();
        verify(member).changePoint(BigDecimal.valueOf(500)); // 1000 - 500
    }

    @Test
    @DisplayName("사용한 포인트 처리 - 실패")
    void processUsedPoint_fail_insufficientPoint() {
        UsedPointRequest request = UsedPointRequest.builder()
                .memberId(1L)
                .usedPoint(BigDecimal.valueOf(10000))
                .build();

        when(memberRepository.findPoint(1L)).thenReturn(BigDecimal.valueOf(100));

        assertThatThrownBy(() -> pointManager.processUsedPoint(request))
                .isInstanceOf(InvalidPointOperationException.class);
    }

    @Test
    @DisplayName("주문 후 포인트 적립처리 - 성공")
    void processOrderPoint_success() {
        PointAfterOrderRequest request = PointAfterOrderRequest.builder()
                .memberId(1L)
                .price(BigDecimal.valueOf(10000))
                .build();

        Member member = mock(Member.class);
        when(member.getGrade()).thenReturn(Grade.builder().gradeName(GradeName.BASIC).pointRate(BigDecimal.valueOf(0.01)).build());
        when(member.getPoint()).thenReturn(BigDecimal.ZERO);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepository.isNotActiveMember(1L)).thenReturn(false);
        when(pointPolicyService.findByPolicyKey("book_default_rate")).thenReturn(
                PointPolicyResponse.builder()
                        .value(BigDecimal.valueOf(0.01))
                        .status(PolicyStatus.ACTIVE)
                        .build());

        when(pointHistoryService.recordPointHistory(any())).thenReturn(mock(PointHistoryResponse.class));

        Optional<PointHistoryResponse> result = pointManager.processOrderPoint(request);

        assertThat(result).isPresent();
        verify(member).changePoint(any());
    }

    @Test
    @DisplayName("주문 후 포인트 적립처리 - 휴면회원 실패")
    void processOrderPoint_fail_inactiveMember() {
        PointAfterOrderRequest request = PointAfterOrderRequest.builder()
                .memberId(1L)
                .price(BigDecimal.valueOf(10000))
                .build();

        when(memberRepository.isNotActiveMember(1L)).thenReturn(true);

        assertThatThrownBy(() -> pointManager.processOrderPoint(request))
                .isInstanceOf(InactiveMemberException.class);
    }

    @Test
    @DisplayName("환불 후 포인트 적립 - 성공")
    void processRefundPoint_success() {
        Member member = mock(Member.class);
        when(member.getPoint()).thenReturn(BigDecimal.ZERO);

        PointAfterOrderRequest request = PointAfterOrderRequest.builder()
                .memberId(1L)
                .price(BigDecimal.valueOf(3000))
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(pointHistoryService.recordPointHistory(any())).thenReturn(mock(PointHistoryResponse.class));

        PointHistoryResponse response = pointManager.processRefundPoint(request);

        assertThat(response).isNotNull();
        verify(member).changePoint(BigDecimal.valueOf(3000));
    }

    @Test
    @DisplayName("이벤트 포인트 처리 - 성공")
    void processEventPoint_success() {
        long memberId = 1L;
        PointReason reason = PointReason.JOIN;

        PointPolicyResponse policy = PointPolicyResponse.builder()
                .policyKey("join_point")
                .value(BigDecimal.valueOf(5000))
                .status(PolicyStatus.ACTIVE)
                .valueType(PointValueType.AMOUNT)
                .build();

        Member member = mock(Member.class);
        when(member.getPoint()).thenReturn(BigDecimal.ZERO);

        when(memberRepository.isNotActiveMember(memberId)).thenReturn(false);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(pointPolicyService.findByPolicyKey("join_point")).thenReturn(policy);
        when(pointHistoryService.recordPointHistory(any())).thenReturn(mock(PointHistoryResponse.class));

        Optional<PointHistoryResponse> result = pointManager.processEventPoint(memberId, reason, null);

        assertThat(result).isPresent();
        verify(member).changePoint(BigDecimal.valueOf(5000));
    }
}
