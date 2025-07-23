package com.nhnacademy.illuwa.domain.point.pointpolicy.exception;

public class DuplicatePointPolicyException extends RuntimeException{
    public DuplicatePointPolicyException(String policyKey) {
        super("이미 존재하는 포인트 정책입니다: " + policyKey);
    }
}
