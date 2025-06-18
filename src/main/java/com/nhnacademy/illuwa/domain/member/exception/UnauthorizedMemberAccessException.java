package com.nhnacademy.illuwa.domain.member.exception;

public class UnauthorizedMemberAccessException extends RuntimeException {
    public UnauthorizedMemberAccessException() {
        super("허용되지 않은 권한입니다.");
    }
}
