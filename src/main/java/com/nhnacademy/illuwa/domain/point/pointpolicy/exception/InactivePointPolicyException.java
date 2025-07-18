package com.nhnacademy.illuwa.domain.point.pointpolicy.exception;

public class InactivePointPolicyException extends RuntimeException{
    public InactivePointPolicyException(String policyKey) {
        super("비활성화된 포인트 정책입니다: " + policyKey);
    }
    public InactivePointPolicyException() {
        super("비활성화된 포인트 정책입니다." );
    }



}
