package com.nhnacademy.illuwa.domain.member.repo;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.member.entity.Member;

import com.nhnacademy.illuwa.domain.member.entity.QMember;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class MemberRepositoryImpl extends QuerydslRepositorySupport implements MemberGradeRepository{
    public MemberRepositoryImpl() {
        super(Member.class);
    }

    QMember member = QMember.member;

    @Override
    public List<Member> findByGrade(Grade grade) {
        return from(member)
                .where(member.grade.eq(grade))
                .fetch();
    }
}
