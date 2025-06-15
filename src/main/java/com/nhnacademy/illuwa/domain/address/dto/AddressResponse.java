package com.nhnacademy.illuwa.domain.address.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private long addressId;
    private String addressName;
    private String recipient;
    private String contact;
    private String addressDetail;
    private boolean isDefault;
}
