package com.nhnacademy.illuwa.domain.pointhistory.service;

import com.nhnacademy.illuwa.domain.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason;

import java.math.BigDecimal;
import java.util.List;

public interface PointHistoryService {

    //포인트 히스토리 기록
    PointHistoryResponse recordPointHistory(long memberId, BigDecimal point, PointReason reason);

    //회원 포인트 히스토리 조회
    List<PointHistoryResponse> getMemberPointHistories(long memberId);
}
