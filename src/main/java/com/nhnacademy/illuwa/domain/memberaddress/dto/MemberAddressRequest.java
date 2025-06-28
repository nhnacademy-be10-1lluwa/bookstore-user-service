package com.nhnacademy.illuwa.domain.memberaddress.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
public class MemberAddressRequest {
    //주소 api
    @NotBlank
    private String postCode;

    @NotBlank
    private String address;

    @NotBlank
    private String detailAddress;

    //사용자 입력칸
    @Size(max = 50, message = "배송지 이름은 50자 이내여야 합니다.")
    private String addressName;

    @NotBlank(message = "수령인은 필수 입력 값입니다.")
    @Size(max = 50, message = "수령인은 50자 이내여야 합니다.")
    private String recipientName;

    @NotBlank(message = "연락처는 필수 입력 값입니다.")
    @Size(max = 50, message = "연락처는 50자 이내여야 합니다.")
    @Pattern(regexp = "^010-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String recipientContact;

    @NotNull(message = "기본 배송지 여부를 선택해주세요.")
    @Builder.Default
    private boolean defaultAddress = true;
}
