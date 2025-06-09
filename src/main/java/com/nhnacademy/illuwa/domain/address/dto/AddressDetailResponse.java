package com.nhnacademy.illuwa.domain.address.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDetailResponse {
    private int addressId;
    private int memberId;
    private String addressName;
    private String recipient;
    private String recipientPhone;
    private String addressDetail;
    private boolean isDefault;
}
