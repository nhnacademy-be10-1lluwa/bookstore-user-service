package com.nhnacademy.illuwa.domain.memberaddress.repo;

import com.nhnacademy.illuwa.domain.memberaddress.entity.MemberAddress;

import java.util.Optional;

public interface CustomMemberAddressRepository{
    Optional<MemberAddress> findDefaultMemberAddress(long memberId);
    void unsetAllDefaultForMember(long memberId);
}
