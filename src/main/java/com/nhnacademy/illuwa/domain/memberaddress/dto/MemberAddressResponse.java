package com.nhnacademy.illuwa.domain.memberaddress.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberAddressResponse {
    private long addressId;
    private String addressName;
    private String recipientName;
    private String recipientContact;
    private String postCode;
    private String address;
    private String detailAddress;
    private boolean isDefault;
}
