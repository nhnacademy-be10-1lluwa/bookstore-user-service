package com.nhnacademy.illuwa.domain.member.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(Long memberId) {
        super("해당 회원을 찾을 수 없습니다: " + memberId);
    }
    public MemberNotFoundException(String message) {
        super("해당 회원을 찾을 수 없습니다.");
    }
    public MemberNotFoundException() {
        super("해당 회원을 찾을 수 없습니다.");
    }
}