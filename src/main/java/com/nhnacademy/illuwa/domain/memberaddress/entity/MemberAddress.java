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
    @Column(name = "address_name")
    private String addressName;

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
    @Column(name = "road_address", nullable = false)
    private String roadAddress;

    @Setter
    @Column(name = "detail_address", nullable = false)
    private String detailAddress;

    @Setter
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public MemberAddress(String addressName, boolean isDefault, String recipientName, String recipientContact, String roadAddress, String detailAddress, Member member) {
        this.addressName = addressName;
        this.recipientName = recipientName;
        this.recipientContact = recipientContact;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.isDefault = isDefault;
        this.member = member;
    }
}
