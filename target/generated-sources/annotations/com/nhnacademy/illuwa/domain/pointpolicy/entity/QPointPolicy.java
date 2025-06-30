package com.nhnacademy.illuwa.domain.pointpolicy.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPointPolicy is a Querydsl query type for PointPolicy
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPointPolicy extends EntityPathBase<PointPolicy> {

    private static final long serialVersionUID = -1693745570L;

    public static final QPointPolicy pointPolicy = new QPointPolicy("pointPolicy");

    public final StringPath description = createString("description");

    public final StringPath policyKey = createString("policyKey");

    public final NumberPath<java.math.BigDecimal> value = createNumber("value", java.math.BigDecimal.class);

    public final EnumPath<com.nhnacademy.illuwa.domain.pointpolicy.entity.enums.PointValueType> valueType = createEnum("valueType", com.nhnacademy.illuwa.domain.pointpolicy.entity.enums.PointValueType.class);

    public QPointPolicy(String variable) {
        super(PointPolicy.class, forVariable(variable));
    }

    public QPointPolicy(Path<? extends PointPolicy> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPointPolicy(PathMetadata metadata) {
        super(PointPolicy.class, metadata);
    }

}

