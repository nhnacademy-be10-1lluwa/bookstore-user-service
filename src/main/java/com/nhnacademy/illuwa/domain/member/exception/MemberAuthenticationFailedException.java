package com.nhnacademy.illuwa.domain.member.exception;

public class MemberAuthenticationFailedException extends RuntimeException {
    public MemberAuthenticationFailedException() {
        super("로그인에 실패했습니다.");
    }
}
