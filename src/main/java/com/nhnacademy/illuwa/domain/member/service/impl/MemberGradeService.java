package com.nhnacademy.illuwa.domain.member.service.impl;

import com.nhnacademy.illuwa.domain.member.dto.MemberGradeUpdateRequest;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberGradeService {
    private final MemberService memberService;

    public int updateGrades(List<MemberGradeUpdateRequest> requests){
        int updatedCount = 0;

        for(MemberGradeUpdateRequest request : requests){
            long memberId = request.getMemberId();
            List<BigDecimal> amounts = request.getNetOrderAmount();

            BigDecimal totalAmount = amounts.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if(memberService.updateMemberGrade(memberId, totalAmount)){
                updatedCount++;
            }
        }
        return updatedCount;
    }
}
