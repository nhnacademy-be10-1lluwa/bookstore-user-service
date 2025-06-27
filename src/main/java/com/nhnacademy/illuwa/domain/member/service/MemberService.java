package com.nhnacademy.illuwa.domain.member.service;

import com.nhnacademy.illuwa.domain.member.dto.MemberLoginRequest;
import com.nhnacademy.illuwa.domain.member.dto.MemberRegisterRequest;
import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.dto.MemberUpdateRequest;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;

import java.math.BigDecimal;
import java.util.List;

public interface MemberService {
    MemberResponse register(MemberRegisterRequest request);

    MemberResponse login(MemberLoginRequest request);

    List<MemberResponse> getAllMembers();

    List<MemberResponse> getAllMembersByStatus(Status status);

    MemberResponse getMemberById(long memberId);

    MemberResponse getMemberByEmail(String email);

    MemberResponse updateMember(long memberId, MemberUpdateRequest newMemberRequest);

    void updateMemberPoint(long memberId, BigDecimal point);

    boolean updateMemberGrade(long memberId, BigDecimal netOrderAmount);

    void checkMemberStatus(long memberId);

    void reactivateMember(long memberId);

    void removeMember(long memberId);
}
