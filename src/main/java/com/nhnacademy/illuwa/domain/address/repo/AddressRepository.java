package com.nhnacademy.illuwa.domain.address.repo;

import com.nhnacademy.illuwa.domain.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    @Query("SELECT a FROM Address a WHERE a.member.memberId = :memberId AND a.isDefault = true")
    Optional<Address> findMemberDefaultAddress(long memberId);

    List<Address> findAllByMember_MemberId(long memberId);
}
