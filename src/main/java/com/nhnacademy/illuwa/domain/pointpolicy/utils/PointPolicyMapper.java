package com.nhnacademy.illuwa.domain.pointpolicy.utils;

import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyCreateRequest;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyResponse;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyUpdateRequest;
import com.nhnacademy.illuwa.domain.pointpolicy.entity.PointPolicy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PointPolicyMapper {
    PointPolicy dtoToEntity(PointPolicyCreateRequest dto);
    PointPolicy dtoToEntity(PointPolicyResponse dto);
    PointPolicyResponse entityToDto(PointPolicy pointPolicy);
    PointPolicy updatePointPolicy(@MappingTarget PointPolicy target, PointPolicyUpdateRequest request);
}
