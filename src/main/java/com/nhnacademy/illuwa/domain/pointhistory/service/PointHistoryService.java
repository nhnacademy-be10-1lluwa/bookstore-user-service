package com.nhnacademy.illuwa.domain.pointhistory.service;

import com.nhnacademy.illuwa.domain.pointhistory.dto.PointAfterOrderRequest;
import com.nhnacademy.illuwa.domain.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.pointhistory.dto.UsedPointRequest;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason;

import java.math.BigDecimal;
import java.util.List;

public interface PointHistoryService {

    //사용한 포인트 차감 히스토리
    PointHistoryResponse processUsedPoint(UsedPointRequest request);

    //주문 포인트적립 히스토리
    PointHistoryResponse processOrderPoint(PointAfterOrderRequest request);

    // 이벤트 기반 포인트 히스토리- JOIN, REVIEW, PHOTO_REVIEW
    PointHistoryResponse processEventPoint(long memberId, PointReason reason);

    //포인트 히스토리 기록
    PointHistoryResponse recordPointHistory(long memberId, BigDecimal point, PointReason reason);

    //회원 포인트 히스토리 조회
    List<PointHistoryResponse> getMemberPointHistories(long memberId);
}
