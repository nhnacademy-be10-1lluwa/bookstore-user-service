package com.nhnacademy.illuwa.domain.pointhistory.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPointHistory is a Querydsl query type for PointHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPointHistory extends EntityPathBase<PointHistory> {

    private static final long serialVersionUID = -530307840L;

    public static final QPointHistory pointHistory = new QPointHistory("pointHistory");

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> memberId = createNumber("memberId", Long.class);

    public final NumberPath<Long> pointHistoryId = createNumber("pointHistoryId", Long.class);

    public final EnumPath<com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason> reason = createEnum("reason", com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason.class);

    public final EnumPath<com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointHistoryType> type = createEnum("type", com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointHistoryType.class);

    public QPointHistory(String variable) {
        super(PointHistory.class, forVariable(variable));
    }

    public QPointHistory(Path<? extends PointHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPointHistory(PathMetadata metadata) {
        super(PointHistory.class, metadata);
    }

}

