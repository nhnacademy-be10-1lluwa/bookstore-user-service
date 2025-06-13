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

    @Setter
    @Column(name = "name",nullable = false)
    private String name;

    @Setter
    @Column(name = "email",nullable = false)
    private String email;

    @Setter
    @Column(name = "order_password",nullable = false)
    private String orderPassword;

    @Setter
    @Column(name = "contact",nullable = false)
    private String contact;

    @Setter
    @JoinColumn(name = "order_id",nullable = false)
    private long orderId;

    @Builder
    public Guest(String name, String email, String orderPassword, String contact, Long orderId) {
        this.name = name;
        this.email = email;
        this.orderPassword = orderPassword;
        this.contact = contact;
        this.orderId = orderId;
    }
}
