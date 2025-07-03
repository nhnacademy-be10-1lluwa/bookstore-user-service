package com.nhnacademy.illuwa.domain.guest.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "guests")
@Getter
@NoArgsConstructor
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_id")
    private long guestId;

    @Column(name = "order_id",nullable = false)
    private long orderId;

    @Column(name = "order_number", nullable = false)
    private String orderNumber;

    @Column(name = "order_password",nullable = false)
    private String orderPassword;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "email",nullable = false)
    private String email;

    @Column(name = "contact",nullable = false)
    private String contact;

    @Builder
    public Guest(long orderId, String orderNumber, String orderPassword, String name, String email, String contact) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.orderPassword = orderPassword;
        this.name = name;
        this.email = email;
        this.contact = contact;
    }
}
