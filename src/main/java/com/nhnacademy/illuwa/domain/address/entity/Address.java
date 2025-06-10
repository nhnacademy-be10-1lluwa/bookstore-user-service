package com.nhnacademy.illuwa.domain.address.entity;

import com.nhnacademy.illuwa.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "address")
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

    private String recipient;

    private String recipientPhone;

    @Column(name = "address_detail")
    private String addressDetail;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

}
