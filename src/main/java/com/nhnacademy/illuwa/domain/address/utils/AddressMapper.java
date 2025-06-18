package com.nhnacademy.illuwa.domain.address.utils;

import com.nhnacademy.illuwa.domain.address.dto.AddressRequest;
import com.nhnacademy.illuwa.domain.address.dto.AddressResponse;
import com.nhnacademy.illuwa.domain.address.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressMapper {
    Address toEntity(AddressRequest request);
    AddressResponse addressToDto(Address address);
    Address updateAddress(@MappingTarget Address target, Address source);
}