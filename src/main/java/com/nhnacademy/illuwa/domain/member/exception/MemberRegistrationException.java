package com.nhnacademy.illuwa.domain.member.exception;

public class MemberRegistrationException extends RuntimeException {
    public MemberRegistrationException(String message) {
        super("회원 가입 중 오류가 발생했습니다: " + message);
    }
}
