package com.nhnacademy.illuwa.domain.address.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {
    private String addressName;
    private String recipient;
    private String contact;
    private String addressDetail;
    private boolean isDefault;
}

