package com.nhnacademy.illuwa.domain.member.repo;

import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CustomMemberRepository{
    List<Member> findByGradeName(GradeName gradeName);
    BigDecimal findPoint(long memberId);
    boolean isNotActiveMember(long memberId);
    Page<Member> findMemberOrderByLastLoginAtOrderDesc(Pageable pageable);
    Page<Member> findMemberByGradeNameOrderByLastLoginAtOrderDesc(GradeName gradeName, Pageable pageable);
    Map<Long, String> getNamesFromIdList(List<Long> memberIds);
}
