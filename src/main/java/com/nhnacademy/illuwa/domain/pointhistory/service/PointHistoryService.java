package com.nhnacademy.illuwa.domain.pointhistory.service;

import com.nhnacademy.illuwa.domain.pointhistory.dto.OrderRequest;
import com.nhnacademy.illuwa.domain.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason;

public interface PointHistoryService {

    PointHistoryResponse recordPointHistory(long memberId, int point, PointReason reason);
    PointHistoryResponse processPointHistory(long memberId, PointReason reason, OrderRequest request);
}
