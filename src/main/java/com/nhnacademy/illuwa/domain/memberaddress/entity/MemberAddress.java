package com.nhnacademy.illuwa.domain.memberaddress.entity;

import com.nhnacademy.illuwa.common.exception.InvalidInputException;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressRequest;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;

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

    @Column(name = "post_code")
    private String postCode;

    @Column(name = "address_name")
    private String addressName;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "detail_address", nullable = false)
    private String detailAddress;

    @Column(name = "default_address")
    private boolean defaultAddress = true;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "recipient_contact", nullable = false)
    private String recipientContact;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

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
        this.member = member;
    }

    public void updateMemberAddress(MemberAddressRequest request) {
        if (request.getPostCode() != null) {
            if (!StringUtils.hasText(request.getPostCode())) {
                throw new InvalidInputException("우편번호는 공백일 수 없어요!");
            }
            this.postCode = request.getPostCode();
        }

        if (request.getAddressName() != null) {
            if (!StringUtils.hasText(request.getAddressName())) {
                throw new InvalidInputException("주소 이름은 공백일 수 없어요!");
            }
            this.addressName = request.getAddressName();
        }

        if (request.getAddress() != null) {
            if (!StringUtils.hasText(request.getAddress())) {
                throw new InvalidInputException("주소는 공백일 수 없어요!");
            }
            this.address = request.getAddress();
        }

        if (request.getDetailAddress() != null) {
            if (!StringUtils.hasText(request.getDetailAddress())) {
                throw new InvalidInputException("상세 주소는 공백일 수 없어요!");
            }
            this.detailAddress = request.getDetailAddress();
        }

        if (this.defaultAddress != request.isDefaultAddress()) {
            this.defaultAddress = request.isDefaultAddress();
        }

        if (request.getRecipientName() != null) {
            if (!StringUtils.hasText(request.getRecipientName())) {
                throw new InvalidInputException("받는 사람 이름은 공백일 수 없어요!");
            }
            this.recipientName = request.getRecipientName();
        }

        if (request.getRecipientContact() != null) {
            if (!StringUtils.hasText(request.getRecipientContact())) {
                throw new InvalidInputException("받는 사람 연락처는 공백일 수 없어요!");
            }
            this.recipientContact = request.getRecipientContact();
        }
    }
}
