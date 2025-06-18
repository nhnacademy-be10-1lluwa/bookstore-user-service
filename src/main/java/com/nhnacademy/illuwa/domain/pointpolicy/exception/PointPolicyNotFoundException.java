package com.nhnacademy.illuwa.domain.pointpolicy.exception;

public class PointPolicyNotFoundException extends RuntimeException{
    public PointPolicyNotFoundException(String policyKey) {
        super("해당 포인트 정책을 찾을 수 없습니다: " + policyKey);
    }
    public PointPolicyNotFoundException() {
        super("해당 포인트 정책을 찾을 수 없습니다." );
    }



}
