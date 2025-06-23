package com.nhnacademy.illuwa.domain.pointhistory.repo;

import com.nhnacademy.illuwa.domain.pointhistory.entity.PointHistory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.awt.*;

@Repository
public class PointHistoryRepositoryImpl extends QuerydslRepositorySupport implements CustomPointHistoryRepository {
    public PointHistoryRepositoryImpl() {
        super(PointHistory.class);
    }


}
