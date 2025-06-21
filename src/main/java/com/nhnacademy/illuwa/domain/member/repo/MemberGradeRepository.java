package com.nhnacademy.illuwa.domain.member.repo;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import java.util.List;

public interface MemberGradeRepository{
    List<Member> findByGrade(Grade grade);
}
