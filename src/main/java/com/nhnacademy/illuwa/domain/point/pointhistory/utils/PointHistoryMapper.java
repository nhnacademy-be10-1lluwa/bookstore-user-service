package com.nhnacademy.illuwa.domain.point.pointhistory.utils;

import com.nhnacademy.illuwa.domain.point.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.PointHistory;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PointHistoryMapper {
    PointHistoryResponse toDto(PointHistory pointHistory);
}
