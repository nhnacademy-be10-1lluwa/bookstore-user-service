package com.nhnacademy.illuwa.domain.member.utils;

import com.nhnacademy.illuwa.domain.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MemberMapper {
    void updateMember(@MappingTarget Member target, Member source);
}
