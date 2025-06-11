package com.nhnacademy.illuwa.domain.member.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(long memberId) {
        super("회원을 찾을 수 없습니다. ID: " + memberId);
    }
}