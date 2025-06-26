package com.nhnacademy.illuwa.domain.memberaddress.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberAddressRequest {
    //주소 api
    @Setter
    @Column(name = "post_code")
    private String postCode;

    @Setter
    @Column(name = "address", nullable = false)
    private String address;

    @Setter
    @Column(name = "detail_address", nullable = false)
    private String detailAddress;

    //사용자 입력칸
    @Size(max = 50, message = "배송지 이름은 50자 이내여야 합니다.")
    private String addressName;

    @NotBlank(message = "수령인은 필수 입력 값입니다.")
    @Size(max = 50, message = "수령인은 50자 이내여야 합니다.")
    private String recipientName;

    @NotBlank(message = "연락처는 필수 입력 값입니다.")
    @Size(max = 50, message = "연락처는 50자 이내여야 합니다.")
    private String recipientContact;

    @NotNull(message = "기본 배송지 여부를 선택해주세요.")
    private Boolean isDefault;
}
