package com.nhnacademy.illuwa.domain.memberaddress.utils;

import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressRequest;
import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressResponse;
import com.nhnacademy.illuwa.domain.memberaddress.entity.MemberAddress;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MemberAddressMapper {
    MemberAddress toEntity(MemberAddressRequest request, Member member);

    MemberAddressResponse toDto(MemberAddress memberAddress);

    MemberAddress updateMemberAddress(@MappingTarget MemberAddress target, MemberAddressRequest request);
}