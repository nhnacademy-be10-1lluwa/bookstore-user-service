package com.nhnacademy.illuwa.domain.member.service;

import com.nhnacademy.illuwa.domain.member.dto.*;
import com.nhnacademy.illuwa.domain.member.entity.Member;
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

    boolean updateMemberGrade(long memberId, BigDecimal netOrderAmount);

    void checkMemberStatus(long memberId);

    void reactivateMember(long memberId);

    void removeMember(long memberId);

    List<MemberResponse> getMembersByBirthMonth(int month);
}
