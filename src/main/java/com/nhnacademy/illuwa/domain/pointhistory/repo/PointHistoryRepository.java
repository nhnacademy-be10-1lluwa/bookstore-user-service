package com.nhnacademy.illuwa.domain.pointhistory.repo;

import com.nhnacademy.illuwa.domain.pointhistory.entity.PointHistory;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointHistoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long>, CustomPointHistoryRepository {
    Page<PointHistory> findAllByMemberIdOrderByCreatedAtDesc(long memberId, Pageable pageable);
    Page<PointHistory> findByMemberIdAndTypeOrderByCreatedAtDesc(long memberId, PointHistoryType type, Pageable pageable);

}
