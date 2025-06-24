package com.nhnacademy.illuwa.domain.pointhistory.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderRequest {
    long memberId;
    long orderId;

    //회원 정보
    BigDecimal netOrderAmount;
    int usedPoint;

    //비회원 개인정보
    String orderNumber;
    String guestName;
    String guestEmail;
    String orderPassword;
    String guestContact;

}
