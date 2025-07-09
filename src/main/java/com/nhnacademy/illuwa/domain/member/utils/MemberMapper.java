package com.nhnacademy.illuwa.domain.member.utils;

import com.nhnacademy.illuwa.domain.member.dto.MemberRegisterRequest;
import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MemberMapper {
    @Mapping(source = "name", target = "name")
    @Mapping(source = "birth", target = "birth")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "contact", target = "contact")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "paycoId", target = "paycoId")
    Member toEntity(MemberRegisterRequest request);

    @Mapping(source = "grade.gradeName", target = "gradeName")
    MemberResponse toDto(Member member);
}