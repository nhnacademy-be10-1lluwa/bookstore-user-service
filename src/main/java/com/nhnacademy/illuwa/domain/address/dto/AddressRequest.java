package com.nhnacademy.illuwa.domain.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {

    @Size(max = 50, message = "배송지 이름은 50자 이내여야 합니다.")
    private String addressName;

    @NotBlank(message = "수령인은 필수 입력 값입니다.")
    @Size(max = 50, message = "수령인은 50자 이내여야 합니다.")
    private String recipient;

    @NotBlank(message = "연락처는 필수 입력 값입니다.")
    @Size(max = 50, message = "연락처는 50자 이내여야 합니다.")
    private String contact;

    @NotBlank(message = "주소는 필수 입력 값입니다.")
    @Size(max = 255, message = "주소는 255자 이내여야 합니다.")
    private String addressDetail;

    @NotNull(message = "기본 배송지 여부를 선택해주세요.")
    private Boolean isDefault;
}
