package com.nhnacademy.illuwa.domain.member.entity.enums;

import java.math.BigDecimal;
import java.util.Arrays;

public enum Grade {
    비회원(BigDecimal.ZERO, BigDecimal.ZERO, null),
    일반(BigDecimal.ZERO, new BigDecimal("100000"), new BigDecimal("0.01")),
    로얄(new BigDecimal("100000"), new BigDecimal("200000"), new BigDecimal("0.02")),
    골드(new BigDecimal("200000"), new BigDecimal("300000"), null),
    플래티넘(new BigDecimal("300000"), null, new BigDecimal("0.03"));

    private final BigDecimal minAmount;     //최소 순수주문금액
    private final BigDecimal maxAmount;     //상한 금액
    private final BigDecimal pointRate;     //적립률

    Grade(BigDecimal minAmount, BigDecimal maxAmount, BigDecimal pointRate) {
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.pointRate = pointRate;
    }

    //범위확인
    public boolean inRange(BigDecimal amount) {
        boolean overMin = amount.compareTo(minAmount) >= 0;
        boolean underMax = (maxAmount == null) || amount.compareTo(maxAmount) < 0;
        return overMin && underMax;
    }

    //금액에 따른 등급산정
    public static Grade calculateByAmount(BigDecimal amount) {
        return Arrays.stream(values())
                .filter(grade -> grade.inRange(amount))
                .findFirst()
                .orElse(비회원);
    }

    //등급에 따른 적립포인트 계산
    public BigDecimal calculatePoint(BigDecimal amount) {
        return amount.multiply(pointRate);
    }

}
