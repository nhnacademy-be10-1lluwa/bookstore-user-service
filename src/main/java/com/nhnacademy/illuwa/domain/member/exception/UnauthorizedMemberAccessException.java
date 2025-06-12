package com.nhnacademy.illuwa.domain.member.exception;

public class UnauthorizedMemberAccessException extends RuntimeException {
    public UnauthorizedMemberAccessException() {
        super("해당 리소스에 접근할 권한이 없습니다.");
    }
}
