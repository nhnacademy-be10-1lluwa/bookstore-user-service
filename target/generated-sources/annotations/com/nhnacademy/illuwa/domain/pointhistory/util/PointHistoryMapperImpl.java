package com.nhnacademy.illuwa.domain.pointhistory.util;

import com.nhnacademy.illuwa.domain.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.pointhistory.entity.PointHistory;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-30T18:04:26+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class PointHistoryMapperImpl implements PointHistoryMapper {

    @Override
    public PointHistoryResponse toDto(PointHistory pointHistory) {
        if ( pointHistory == null ) {
            return null;
        }

        PointHistoryResponse pointHistoryResponse = new PointHistoryResponse();

        pointHistoryResponse.setAmount( pointHistory.getAmount() );
        pointHistoryResponse.setReason( pointHistory.getReason() );
        pointHistoryResponse.setType( pointHistory.getType() );
        pointHistoryResponse.setCreatedAt( pointHistory.getCreatedAt() );

        return pointHistoryResponse;
    }
}
