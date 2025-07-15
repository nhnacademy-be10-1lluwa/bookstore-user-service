package com.nhnacademy.illuwa.domain.member.service;

import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.member.dto.*;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface MemberService {
    MemberResponse register(MemberRegisterRequest request);

    MemberResponse login(MemberLoginRequest request);

    List<MemberResponse> getAllMembers();

    Page<MemberResponse> getPagedAllMembers(Pageable pageable);

    Page<MemberResponse> getPagedAllMembersByGradeName(GradeName gradeName, Pageable pageable);

    List<MemberResponse> getMembersByStatus(Status status);

    List<MemberResponse> getMembersByGradeName(GradeName gradeName);

    MemberResponse getMemberById(long memberId);

    InactiveCheckResponse getInactiveMemberInfoByEmail(String email);

    MemberResponse updateMember(long memberId, MemberUpdateRequest newMemberRequest);

    boolean checkPassword(long memberId, String inputPassword);

    boolean updateMemberGrade(long memberId, BigDecimal netOrderAmount);

    void updateMemberStatus(long memberId);

    void reactivateMember(long memberId);

    void removeMember(long memberId);

    List<MemberResponse> getMembersByBirthMonth(int month);
}
