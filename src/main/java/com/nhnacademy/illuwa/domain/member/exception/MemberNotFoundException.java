package com.nhnacademy.illuwa.domain.member.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String message) {
        super(message);
    }
    public MemberNotFoundException(Long memberId) {
        super("존재하지 않는 회원입니다: " + memberId);
    }
}