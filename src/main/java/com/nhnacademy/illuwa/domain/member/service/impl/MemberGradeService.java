package com.nhnacademy.illuwa.domain.member.service.impl;

import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.member.dto.MemberGradeUpdateRequest;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.point.utils.PointManager;
import com.nhnacademy.illuwa.domain.point.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums.PointReason;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberGradeService {
    private final MemberService memberService;
    private final PointManager pointManager;

    public int updateGrades(List<MemberGradeUpdateRequest> requests){
        int updatedCount = 0;

        for(MemberGradeUpdateRequest request : requests){
            long memberId = request.getMemberId();
            BigDecimal totalAmount = request.getNetOrderAmount();
            if(memberService.updateMemberGrade(memberId, totalAmount)){
                updatedCount++;
            }
        }
        return updatedCount;
    }

    public List<PointHistoryResponse> givePointsByGrade(GradeName gradeName, BigDecimal point){
        return memberService.getMembersByGradeName(gradeName).stream()
                .map(member -> pointManager.processEventPoint(member.getMemberId(), PointReason.GRADE_EVENT, point))
                .flatMap(Optional::stream)
                .toList();
    }
}