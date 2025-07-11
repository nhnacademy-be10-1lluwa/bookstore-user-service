package com.nhnacademy.illuwa.domain.pointhistory.entity.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum PointReason {
    PURCHASE("book_default_rate"),     // 구매 적립
    USED_IN_ORDER(null),  //사용
    JOIN("join_point"),   // 회원가입
    REVIEW("review_point"), // 일반리뷰
    PHOTO_REVIEW("photo_review_point"); // 포토리뷰

    private final String policyKey;

    PointReason(String policyKey){
        this.policyKey = policyKey;
    }

    public Optional<String> getPolicyKey(){
        return Optional.ofNullable(policyKey);
    }

    public static PointReason from(String value) {
        return Arrays.stream(values())
                .filter(reason -> reason.name().equalsIgnoreCase(value.replace("-", "_")))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid reason: " + value));
    }
}