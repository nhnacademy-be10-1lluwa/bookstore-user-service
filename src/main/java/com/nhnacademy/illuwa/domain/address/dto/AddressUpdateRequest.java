package com.nhnacademy.illuwa.domain.address.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressUpdateRequest {
    private String addressName;
    private String recipient;
    private String recipientPhone;
    private String addressDetail;
    private boolean isDefault;
}
