package com.nhnacademy.illuwa.domain.pointpolicy.utils;

import com.nhnacademy.illuwa.domain.address.dto.AddressRequest;
import com.nhnacademy.illuwa.domain.address.dto.AddressResponse;
import com.nhnacademy.illuwa.domain.address.entity.Address;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyResponse;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyUpdateRequest;
import com.nhnacademy.illuwa.domain.pointpolicy.entity.PointPolicy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PointPolicyMapper {
    PointPolicyResponse pointPolicyToDto(PointPolicy pointPolicy);
    void updatePointPolicy(@MappingTarget PointPolicy target, PointPolicyUpdateRequest request);
}