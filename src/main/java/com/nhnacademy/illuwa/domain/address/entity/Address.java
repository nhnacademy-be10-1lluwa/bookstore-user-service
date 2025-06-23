package com.nhnacademy.illuwa.domain.address.entity;

import com.nhnacademy.illuwa.domain.guest.entity.Guest;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "address")
@Getter
@NoArgsConstructor
@Entity
@EqualsAndHashCode
public class Address {
    @Id
    @GeneratedValue
    @Column(name = "address_id")
    private long addressId;

    @Setter
    @Column(name = "address_name", nullable = true)
    private String addressName = "기본 배송지";

    @Setter
    @Column(name = "recipient", nullable = false)
    private String recipient;

    @Setter
    @Column(name = "contact", nullable = false)
    private String contact;

    @Setter
    @Column(name = "address_detail", nullable = false)
    private String addressDetail;

    @Setter
    @Column(name = "is_default")
    private boolean isDefault = true;

    @Setter
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Setter
    @OneToOne
    @JoinColumn(name = "guest_id")
    private Guest guest;

    @Builder
    public Address(String addressName, String recipient, String contact, String addressDetail, boolean isDefault, Member member, Guest guest) {
        this.addressName = addressName;
        this.recipient = recipient;
        this.contact = contact;
        this.addressDetail = addressDetail;
        this.isDefault = isDefault;
        this.member = member;
        this.guest = guest;
    }
}
