package com.nhnacademy.illuwa.domain.address.utils;

import com.nhnacademy.illuwa.domain.address.dto.AddressRequest;
import com.nhnacademy.illuwa.domain.address.dto.AddressResponse;
import com.nhnacademy.illuwa.domain.address.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressMapper {/*
    @Mapping(source = "name", target = "name")
    @Mapping(source = "birth", target = "birth")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "contact", target = "contact")*/
    Address toEntity(AddressRequest request);
    AddressResponse addressToDto(Address address);
    void updateAddress(@MappingTarget Address target, Address source);
}