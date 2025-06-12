package com.nhnacademy.illuwa.domain.member.service;

import com.nhnacademy.illuwa.domain.member.dto.MemberLoginRequest;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;

public interface MemberService {
    Member register(Member member);

    @Transactional
    Member login(MemberLoginRequest request);

    Member getMemberById(long memberId);

    @Transactional
    void updateMember(Member member);

    @Transactional
    void updateNetOrderAmountAndChangeGrade(long memberId, BigDecimal netOrderAmount);

    @Transactional
    void updateMemberStatus(long memberId);

    @Transactional
    void removeMember(long memberId);
}
