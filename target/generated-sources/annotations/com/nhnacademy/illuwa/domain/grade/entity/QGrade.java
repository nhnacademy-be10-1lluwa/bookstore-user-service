package com.nhnacademy.illuwa.domain.grade.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QGrade is a Querydsl query type for Grade
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGrade extends EntityPathBase<Grade> {

    private static final long serialVersionUID = -2068876898L;

    public static final QGrade grade = new QGrade("grade");

    public final NumberPath<Long> gradeId = createNumber("gradeId", Long.class);

    public final EnumPath<com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName> gradeName = createEnum("gradeName", com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName.class);

    public final NumberPath<java.math.BigDecimal> maxAmount = createNumber("maxAmount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> minAmount = createNumber("minAmount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> pointRate = createNumber("pointRate", java.math.BigDecimal.class);

    public final NumberPath<Long> priority = createNumber("priority", Long.class);

    public QGrade(String variable) {
        super(Grade.class, forVariable(variable));
    }

    public QGrade(Path<? extends Grade> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGrade(PathMetadata metadata) {
        super(Grade.class, metadata);
    }

}

