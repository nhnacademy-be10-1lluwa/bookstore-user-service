package com.nhnacademy.illuwa.domain.member.service;

import com.nhnacademy.illuwa.domain.member.dto.MemberLoginRequest;
import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.dto.MemberUpdateRequest;
import com.nhnacademy.illuwa.domain.member.entity.Member;

import java.math.BigDecimal;
import java.util.List;

public interface MemberService {
    MemberResponse register(Member member);

    MemberResponse login(MemberLoginRequest request);

    List<MemberResponse> getAllMembers();

    MemberResponse getMemberById(Long memberId);

    MemberResponse updateMember(Long memberId, MemberUpdateRequest newMemberRequest);

    void updateMemberGrade(Long memberId, BigDecimal netOrderAmount);

    void checkMemberStatus(Long memberId);

    void reactivateMember(Long memberId);

    void removeMember(Long memberId);
}
