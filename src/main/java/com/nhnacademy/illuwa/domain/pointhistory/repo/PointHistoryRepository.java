package com.nhnacademy.illuwa.domain.pointhistory.repo;

import com.nhnacademy.illuwa.domain.pointhistory.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long>, CustomPointHistoryRepository {
}
