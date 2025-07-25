package com.nhnacademy.illuwa.domain.memberaddress.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberAddressResponse {
    private long memberAddressId;
    private long memberId;
    private String addressName;
    private String recipientName;
    private String recipientContact;
    private String postCode;
    private String address;
    private String detailAddress;
    private boolean defaultAddress;
    private LocalDateTime createdAt;
    boolean forcedDefaultAddress;
}
