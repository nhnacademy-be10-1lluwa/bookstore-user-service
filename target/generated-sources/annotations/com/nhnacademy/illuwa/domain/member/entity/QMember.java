package com.nhnacademy.illuwa.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1980262156L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMember member = new QMember("member1");

    public final DatePath<java.time.LocalDate> birth = createDate("birth", java.time.LocalDate.class);

    public final StringPath contact = createString("contact");

    public final StringPath email = createString("email");

    public final com.nhnacademy.illuwa.domain.grade.entity.QGrade grade;

    public final DateTimePath<java.time.LocalDateTime> lastLoginAt = createDateTime("lastLoginAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> memberId = createNumber("memberId", Long.class);

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final NumberPath<java.math.BigDecimal> point = createNumber("point", java.math.BigDecimal.class);

    public final EnumPath<com.nhnacademy.illuwa.domain.member.entity.enums.Role> role = createEnum("role", com.nhnacademy.illuwa.domain.member.entity.enums.Role.class);

    public final EnumPath<com.nhnacademy.illuwa.domain.member.entity.enums.Status> status = createEnum("status", com.nhnacademy.illuwa.domain.member.entity.enums.Status.class);

    public QMember(String variable) {
        this(Member.class, forVariable(variable), INITS);
    }

    public QMember(Path<? extends Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMember(PathMetadata metadata, PathInits inits) {
        this(Member.class, metadata, inits);
    }

    public QMember(Class<? extends Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.grade = inits.isInitialized("grade") ? new com.nhnacademy.illuwa.domain.grade.entity.QGrade(forProperty("grade")) : null;
    }

}

