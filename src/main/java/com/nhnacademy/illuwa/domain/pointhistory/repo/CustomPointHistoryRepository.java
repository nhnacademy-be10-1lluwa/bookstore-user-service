package com.nhnacademy.illuwa.domain.pointhistory.repo;

import com.nhnacademy.illuwa.domain.pointhistory.entity.PointHistory;

import java.time.LocalDate;
import java.util.List;

public interface CustomPointHistoryRepository {
    List<PointHistory> findByMemberIdOrderByCreatedAtDesc(long memberId);
    List<PointHistory> findByPointTypeEarn(long memberId);
    List<PointHistory> findByPointTypeUse(long memberId);
    List<PointHistory> findByDate(long memberId, LocalDate startDate, LocalDate endDate);
}
