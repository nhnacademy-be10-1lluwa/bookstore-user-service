package com.nhnacademy.illuwa.domain.memberaddress.entity;

import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "member_address")
@Getter
@NoArgsConstructor
@Entity
public class MemberAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_address_id")
    private long memberAddressId;

    @Column(name = "post_code", nullable = false)
    private String postCode;

    @Column(name = "address_name")
    private String addressName;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "detail_address", nullable = false)
    private String detailAddress;

    @Column(name = "default_address", nullable = false)
    private boolean defaultAddress = true;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "recipient_contact", nullable = false)
    private String recipientContact;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    @Builder
    public MemberAddress(String postCode,
                         String addressName,
                         String address,
                         String detailAddress,
                         boolean defaultAddress,
                         String recipientName,
                         String recipientContact,
                         Member member) {

        this.postCode = postCode;
        this.addressName = addressName;
        this.recipientName = recipientName;
        this.recipientContact = recipientContact;
        this.address = address;
        this.detailAddress = detailAddress;
        this.defaultAddress = defaultAddress;
        this.createdAt = LocalDateTime.now();
        this.member = member;
    }

    public void changeAddressName(String addressName){
        this.addressName = addressName;
    }

    public void changeDefaultAddress(boolean defaultAddress){
        if(this.defaultAddress != defaultAddress)
        {
            this.defaultAddress = defaultAddress;
        }
    }

    public void updateMemberAddress(MemberAddressRequest request) {
        if (request.getPostCode() != null) {
            this.postCode = request.getPostCode();
        }
        if (request.getAddressName() != null) {
            this.addressName = request.getAddressName();
        }
        if (request.getAddress() != null) {
            this.address = request.getAddress();
        }
        if (request.getDetailAddress() != null) {
            this.detailAddress = request.getDetailAddress();
        }
        if (this.defaultAddress != request.isDefaultAddress()) {
            this.defaultAddress = request.isDefaultAddress();
        }
        if (request.getRecipientName() != null) {
            this.recipientName = request.getRecipientName();
        }
        if (request.getRecipientContact() != null) {
            this.recipientContact = request.getRecipientContact();
        }
    }
}
