package com.nhnacademy.illuwa.domain.memberaddress.repo;

import com.nhnacademy.illuwa.domain.memberaddress.entity.MemberAddress;
import com.nhnacademy.illuwa.domain.memberaddress.entity.QMemberAddress;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberAddressRepositoryImpl implements CustomMemberAddressRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<MemberAddress> findDefaultMemberAddress(long memberId){
        QMemberAddress memberAddress = QMemberAddress.memberAddress;
        return Optional.ofNullable(
                queryFactory.selectFrom(memberAddress)
                        .where(
                                memberAddress.member.memberId.eq(memberId)
                                .and(memberAddress.isDefault.eq(true))
                        )
                        .fetchOne()
                );
    }

    @Override
    public void unsetAllDefaultForMember(long memberId){
        QMemberAddress memberAddress = QMemberAddress.memberAddress;
        queryFactory.update(memberAddress)
                .set(memberAddress.isDefault, false)
                .where(memberAddress.member.memberId.eq(memberId))
                .execute();
    }

}
