package com.nhnacademy.illuwa.domain.member.repo;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.member.entity.Member;

import com.nhnacademy.illuwa.domain.member.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberGradeRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findByGrade(Grade grade) {
        QMember member = QMember.member;
        return queryFactory.selectFrom(member)
                .where(member.grade.eq(grade))
                .fetch();
    }
}
