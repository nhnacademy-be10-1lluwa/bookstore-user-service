package com.nhnacademy.illuwa.domain.member.service.impl;

import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberGradeService {
    private final MemberService memberService;

    @Scheduled(cron = "0 0 10 1 * ?",zone = "Asia/Seoul")
    public void updateAllMembersGrade(){
        List<MemberResponse> activeMembers = memberService.getAllMembersByStatus(Status.ACTIVE);
        activeMembers
                .forEach(memberResponse ->
                        memberService.updateMemberGrade(memberResponse.getMemberId(), BigDecimal.ZERO)
                );
                //TODO 추후 순수주문금액 받아오는 방법 확정되면 수정하기
    }

}
