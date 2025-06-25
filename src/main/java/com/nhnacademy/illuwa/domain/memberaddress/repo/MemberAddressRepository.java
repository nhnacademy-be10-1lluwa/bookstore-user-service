package com.nhnacademy.illuwa.domain.memberaddress.repo;

import com.nhnacademy.illuwa.domain.memberaddress.entity.MemberAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberAddressRepository extends JpaRepository<MemberAddress, Long> {
    @Query("SELECT a FROM MemberAddress a WHERE a.member.memberId = :memberId AND a.isDefault = true")
    Optional<MemberAddress> findDefaultMemberAddress(long memberId);

    List<MemberAddress> findAllByMember_MemberId(long memberId);
}
