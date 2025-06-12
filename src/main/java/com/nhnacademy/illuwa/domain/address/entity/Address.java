package com.nhnacademy.illuwa.domain.address.entity;

import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.guest.entity.Guest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "address")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @Id
    @GeneratedValue
    @Column(name = "address_id")
    private long addressId;

    @Column(name = "address_name", nullable = true)
    private String addressName = "기본 배송지";

    @Column(name = "recipient")
    private String recipient;

    @Column(name = "contact")
    private String contact;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = true;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = true)
    private Member member;

    @OneToOne
    @JoinColumn(name = "guest_id", nullable = true)
    private Guest guest;
}
