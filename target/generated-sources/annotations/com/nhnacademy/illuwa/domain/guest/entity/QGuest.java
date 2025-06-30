package com.nhnacademy.illuwa.domain.guest.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QGuest is a Querydsl query type for Guest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGuest extends EntityPathBase<Guest> {

    private static final long serialVersionUID = 2094005054L;

    public static final QGuest guest = new QGuest("guest");

    public final StringPath contact = createString("contact");

    public final StringPath email = createString("email");

    public final NumberPath<Long> guestId = createNumber("guestId", Long.class);

    public final StringPath name = createString("name");

    public final StringPath orderNumber = createString("orderNumber");

    public final StringPath orderPassword = createString("orderPassword");

    public QGuest(String variable) {
        super(Guest.class, forVariable(variable));
    }

    public QGuest(Path<? extends Guest> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGuest(PathMetadata metadata) {
        super(Guest.class, metadata);
    }

}

