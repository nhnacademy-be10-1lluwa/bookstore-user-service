package com.nhnacademy.illuwa.domain.pointhistory.service;

import com.nhnacademy.illuwa.domain.pointhistory.dto.OrderRequest;
import com.nhnacademy.illuwa.domain.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason;

import java.math.BigDecimal;

public interface PointHistoryService {

    //포인트계산-생성까지의 총괄메서드
    PointHistoryResponse processPointHistory(long memberId, PointReason reason, OrderRequest request);

    //포인트 히스토리 생성
    PointHistoryResponse recordPointHistory(long memberId, BigDecimal point, PointReason reason);
}
