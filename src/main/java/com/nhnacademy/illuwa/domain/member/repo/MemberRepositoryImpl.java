package com.nhnacademy.illuwa.domain.member.repo;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.member.entity.Member;

import com.nhnacademy.illuwa.domain.member.entity.QMember;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
        return   queryFactory.select(member.point)
                .from(member)
                .where(member.memberId.eq(memberId))
                .fetchOne();
    }
    public boolean isNotActiveMember(long memberId) {
        QMember member = QMember.member;
        Member result = queryFactory.selectFrom(member)
                .where(member.memberId.eq(memberId),
                        member.status.eq(Status.ACTIVE))
                .fetchFirst();

        return result == null;
    }

    @Override
    public Page<Member> findActiveMemberOrderByLastLoginAtOrderDesc(Pageable pageable) {
        QMember member = QMember.member;

        List<Member> content = queryFactory
                .selectFrom(member)
                .where(
                        member.status.eq(Status.ACTIVE),
                        member.role.ne(Role.ADMIN)
                )
                .orderBy(member.lastLoginAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(member.count())
                .from(member)
                .where(
                        member.status.eq(Status.ACTIVE),
                        member.role.ne(Role.ADMIN)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}
