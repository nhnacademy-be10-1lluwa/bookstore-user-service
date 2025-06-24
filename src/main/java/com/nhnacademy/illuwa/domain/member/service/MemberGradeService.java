package com.nhnacademy.illuwa.domain.member.service;

import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberGradeService {
    private final MemberService memberService;

    @Scheduled(cron = "0 0 10 1 * ?",zone = "Asia/Seoul")
    public void updateAllMembersGrade(){
        List<MemberResponse> activeMembers = memberService.getAllMembersByStatus(Status.ACTIVE);
        activeMembers.stream()
                .map(memberService.)
    }

}
