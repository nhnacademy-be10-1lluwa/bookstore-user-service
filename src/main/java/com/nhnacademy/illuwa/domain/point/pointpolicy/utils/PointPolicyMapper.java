package com.nhnacademy.illuwa.domain.point.pointpolicy.utils;

import com.nhnacademy.illuwa.domain.point.pointpolicy.dto.PointPolicyCreateRequest;
import com.nhnacademy.illuwa.domain.point.pointpolicy.dto.PointPolicyResponse;
import com.nhnacademy.illuwa.domain.point.pointpolicy.entity.PointPolicy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PointPolicyMapper {
    PointPolicy toEntity(PointPolicyCreateRequest dto);
    PointPolicyResponse toDto(PointPolicy pointPolicy);
}
