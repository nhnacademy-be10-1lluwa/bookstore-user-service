package com.nhnacademy.illuwa.domain.pointhistory.service;

import com.nhnacademy.illuwa.domain.pointhistory.dto.PointHistoryRequest;
import com.nhnacademy.illuwa.domain.pointhistory.dto.PointHistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PointHistoryService {

    //포인트 히스토리 기록
    PointHistoryResponse recordPointHistory(PointHistoryRequest request);

    //회원 포인트 히스토리 조회
    List<PointHistoryResponse> getMemberPointHistories(long memberId);
    Page<PointHistoryResponse> getPagedMemberPointHistories(long memberId, Pageable pageable);
}
