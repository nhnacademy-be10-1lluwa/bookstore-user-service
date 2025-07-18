package com.nhnacademy.illuwa.domain.point.pointhistory.repo;

import com.nhnacademy.illuwa.domain.point.pointhistory.entity.PointHistory;
import com.nhnacademy.illuwa.domain.pointhistory.entity.QPointHistory;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums.PointHistoryType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements CustomPointHistoryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<PointHistory> findByMemberIdOrderByCreatedAtDesc(long memberId) {
    QPointHistory pointHistory = QPointHistory.pointHistory;

        return queryFactory.selectFrom(pointHistory)
                .where(pointHistory.memberId.eq(memberId))
                .orderBy(pointHistory.createdAt.desc())
                .fetch();
    }

    @Override
    public List<PointHistory> findByPointTypeEarn(long memberId) {
        QPointHistory pointHistory = QPointHistory.pointHistory;

        return queryFactory.selectFrom(pointHistory)
                .where(pointHistory.memberId.eq(memberId)
                .and(pointHistory.type.eq(PointHistoryType.EARN)))
                .orderBy(pointHistory.createdAt.desc())
                .fetch();
    }

    @Override
    public List<PointHistory> findByPointTypeUse(long memberId) {
        QPointHistory pointHistory = QPointHistory.pointHistory;

        return queryFactory.selectFrom(pointHistory)
                .where(pointHistory.memberId.eq(memberId)
                        .and(pointHistory.type.eq(PointHistoryType.DEDUCT)))
                .orderBy(pointHistory.createdAt.desc())
                .fetch();
    }

    @Override
    public List<PointHistory> findByDate(long memberId, LocalDate startDate, LocalDate endDate) {
        QPointHistory pointHistory = QPointHistory.pointHistory;

        return queryFactory.selectFrom(pointHistory)
                .where(pointHistory.memberId.eq(memberId)
                .and(pointHistory.createdAt.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX))))
                .orderBy(pointHistory.createdAt.desc())
                .fetch();
    }
}
