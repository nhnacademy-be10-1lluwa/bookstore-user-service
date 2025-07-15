package com.nhnacademy.illuwa.domain.member.service.impl;

import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.member.dto.MemberGradeUpdateRequest;
import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.point.util.PointManager;
import com.nhnacademy.illuwa.domain.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointHistoryType;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MemberGradeServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private PointManager pointManager;

    @InjectMocks
    private MemberGradeService memberGradeService;

    private GradeName basicGrade;
    private GradeName goldGrade;

    @BeforeAll
    void beforeAll() {
        MockitoAnnotations.openMocks(this);
        basicGrade = GradeName.BASIC;
        goldGrade = GradeName.GOLD;
    }

    @Test
    @DisplayName("회원 등급 업데이트: 일부만 성공한 경우")
    void testUpdateGrades_PartiallyUpdated() {
        MemberGradeUpdateRequest req1 = new MemberGradeUpdateRequest(1L, new BigDecimal("100000"));
        MemberGradeUpdateRequest req2 = new MemberGradeUpdateRequest(2L, new BigDecimal("500000"));

        when(memberService.updateMemberGrade(1L, req1.getNetOrderAmount())).thenReturn(true);
        when(memberService.updateMemberGrade(2L, req2.getNetOrderAmount())).thenReturn(false);

        int updatedCount = memberGradeService.updateGrades(List.of(req1, req2));

        assertThat(updatedCount).isEqualTo(1);
        verify(memberService).updateMemberGrade(1L, req1.getNetOrderAmount());
        verify(memberService).updateMemberGrade(2L, req2.getNetOrderAmount());
    }

    @Test
    @DisplayName("등급별 포인트 지급 성공 테스트")
    void testGivePointsByGrade_Success() {
        BigDecimal point = new BigDecimal("100");

        MemberResponse member1 = createMemberResponse(1L, "user1", goldGrade);
        MemberResponse member2 = createMemberResponse(2L, "user2", goldGrade);

        when(memberService.getMembersByGradeName(goldGrade)).thenReturn(List.of(member1, member2));

        PointHistoryResponse response1 = new PointHistoryResponse(1L, PointHistoryType.EARN, PointReason.GRADE_EVENT, point, member1.getPoint().add(point), LocalDateTime.now());
        PointHistoryResponse response2 = new PointHistoryResponse(2L, PointHistoryType.EARN, PointReason.GRADE_EVENT, point, member2.getPoint().add(point), LocalDateTime.now());

        when(pointManager.processEventPoint(1L, PointReason.GRADE_EVENT, point))
                .thenReturn(Optional.of(response1));
        when(pointManager.processEventPoint(2L, PointReason.GRADE_EVENT, point))
                .thenReturn(Optional.of(response2));

        List<PointHistoryResponse> result = memberGradeService.givePointsByGrade(goldGrade, point);

        assertThat(result).hasSize(2)
                .extracting(PointHistoryResponse::getMemberId)
                .containsExactlyInAnyOrder(1L, 2L);

        verify(memberService).getMembersByGradeName(goldGrade);
        verify(pointManager).processEventPoint(1L, PointReason.GRADE_EVENT, point);
        verify(pointManager).processEventPoint(2L, PointReason.GRADE_EVENT, point);
    }

    @Test
    @DisplayName("등급별 포인트 지급: 일부 실패 (Optional.empty())")
    void testGivePointsByGrade_PartialFailure() {
        BigDecimal point = new BigDecimal("50");

        MemberResponse member1 = createMemberResponse(1L, "user1", basicGrade);
        MemberResponse member2 = createMemberResponse(2L, "user2", basicGrade);

        when(memberService.getMembersByGradeName(basicGrade)).thenReturn(List.of(member1, member2));

        PointHistoryResponse response1 = new PointHistoryResponse(1L, PointHistoryType.EARN, PointReason.GRADE_EVENT, point, member1.getPoint().add(point), LocalDateTime.now());

        when(pointManager.processEventPoint(1L, PointReason.GRADE_EVENT, point))
                .thenReturn(Optional.of(response1));
        when(pointManager.processEventPoint(2L, PointReason.GRADE_EVENT, point))
                .thenReturn(Optional.empty());

        List<PointHistoryResponse> result = memberGradeService.givePointsByGrade(basicGrade, point);

        assertThat(result).hasSize(1)
                .extracting(PointHistoryResponse::getMemberId)
                .containsExactly(1L);
    }

    private MemberResponse createMemberResponse(Long memberId, String name, GradeName gradeName) {
        return MemberResponse.builder()
                .memberId(memberId)
                .paycoId("payco-" + memberId)
                .name(name)
                .email(name + "@illuwa.com")
                .gradeName(gradeName.name())
                .point(BigDecimal.valueOf(1000))
                .build();
    }
}
