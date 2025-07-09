package com.nhnacademy.illuwa.domain.member.repo;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface CustomMemberRepository{
    List<Member> findByGrade(Grade grade);
    BigDecimal findPoint(long memberId);
    boolean isNotActiveMember(long memberId);
    Page<Member> findActiveMemberOrderByLastLoginAtOrderDesc(Pageable pageable);
}
