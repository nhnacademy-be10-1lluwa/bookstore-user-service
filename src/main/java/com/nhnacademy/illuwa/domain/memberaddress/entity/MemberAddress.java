package com.nhnacademy.illuwa.domain.memberaddress.entity;

import com.nhnacademy.illuwa.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "member_address")
@Getter
@NoArgsConstructor
@Entity
@EqualsAndHashCode
public class MemberAddress {
    @Id
    @GeneratedValue
    @Column(name = "member_address_id")
    private long memberAddressId;

    @Setter
    @Column(name = "post_code")
    private String postCode;

    @Setter
    @Column(name = "address_name")
    private String addressName;

    @Setter
    @Column(name = "address", nullable = false)
    private String address;

    @Setter
    @Column(name = "detail_address", nullable = false)
    private String detailAddress;

    @Setter
    @Column(name = "is_default")
    private boolean isDefault = true;

    @Setter
    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Setter
    @Column(name = "recipient_contact", nullable = false)
    private String recipientContact;


    @Setter
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public MemberAddress(String postCode,
                         String addressName,
                         String address,
                         String detailAddress,
                         boolean isDefault,
                         String recipientName,
                         String recipientContact,
                         Member member) {

        this.addressName = addressName;
        this.recipientName = recipientName;
        this.recipientContact = recipientContact;
        this.address = address;
        this.detailAddress = detailAddress;
        this.isDefault = isDefault;
        this.member = member;
    }
}
