package com.nhnacademy.illuwa.domain.memberaddress.repo;

import com.nhnacademy.illuwa.domain.memberaddress.entity.MemberAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberAddressRepository extends JpaRepository<MemberAddress, Long>, CustomMemberAddressRepository {
    int countAllByMember_MemberId(long memberId);
    List<MemberAddress> findAllByMember_MemberId(long memberId);
}
