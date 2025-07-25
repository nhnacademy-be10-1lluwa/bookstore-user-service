package com.nhnacademy.illuwa.domain.memberaddress.repo;

import com.nhnacademy.illuwa.domain.memberaddress.entity.MemberAddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberAddressRepository extends JpaRepository<MemberAddress, Long>, CustomMemberAddressRepository {
    int countAllByMember_MemberId(long memberId);

    List<MemberAddress> findAllByMember_MemberId(long memberId);

    Page<MemberAddress> findAllByMember_MemberId(Long memberId, Pageable pageable);

    List<MemberAddress> findAllByMember_MemberIdOrderByCreatedAtAsc(long memberId);
}
