package com.nhnacademy.illuwa.domain.member.service;

import com.nhnacademy.illuwa.domain.member.dto.MemberLoginRequest;
import com.nhnacademy.illuwa.domain.member.entity.Member;

import java.math.BigDecimal;

public interface MemberService {
    Member register(Member member);

    Member login(MemberLoginRequest request);

    Member getMemberById(long memberId);

    void updateMember(long memberId, Member newMember);

    void updateNetOrderAmountAndChangeGrade(long memberId, BigDecimal netOrderAmount);

    void checkMemberInactive(long memberId);

    void reactivateMember(long memberId);

    void removeMember(long memberId);
}
