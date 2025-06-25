package com.nhnacademy.illuwa.domain.memberaddress.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberAddressResponse {
    private long addressId;
    private String addressName;
    private String recipient;
    private String contact;
    private String addressDetail;
    private boolean isDefault;
}
