package com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums;

import java.util.Optional;

public enum PointReason {
    PURCHASE("book_default_rate"),     // 구매 적립
    USED_IN_ORDER(null),  //사용
    REFUND(null),         //환불
    JOIN("join_point"),   // 회원가입
    REVIEW("review_point"), // 일반리뷰
    PHOTO_REVIEW("photo_review_point"), // 포토리뷰
    GRADE_EVENT("grade_event");  //등급별 이벤트포인트

    private final String policyKey;

    PointReason(String policyKey){
        this.policyKey = policyKey;
    }

    public Optional<String> getPolicyKey(){
        return Optional.ofNullable(policyKey);
    }
}