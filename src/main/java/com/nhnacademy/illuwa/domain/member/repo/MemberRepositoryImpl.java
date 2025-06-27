package com.nhnacademy.illuwa.domain.member.repo;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.member.entity.Member;

import com.nhnacademy.illuwa.domain.member.entity.QMember;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements CustomMemberRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findByGrade(Grade grade) {
        QMember member = QMember.member;
        return queryFactory.selectFrom(member)
                .where(member.grade.eq(grade))
                .fetch();
    }

    @Override
    public BigDecimal findPoint(long memberId) {
        QMember member = QMember.member;

        return (BigDecimal) queryFactory.select(member.point)
                .from(member)
                .where(member.memberId.eq(memberId))
                .fetch();
    }

    @Override
    public boolean isNotActiveMember(long memberId) {
        QMember member = QMember.member;
        return queryFactory.selectFrom(member)
                .where(member.status.eq(Status.ACTIVE)
                        .and(member.memberId.eq(memberId)))
                .fetchFirst() == null;
    }
}
