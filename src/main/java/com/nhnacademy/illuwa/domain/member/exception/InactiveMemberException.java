package com.nhnacademy.illuwa.domain.member.exception;

public class InactiveMemberException extends RuntimeException {
    public InactiveMemberException(long memberId) {
        super("해당 회원은 휴면 회원입니다: " + memberId);
    }
    public InactiveMemberException() {
        super("해당 회원은 휴면 회원입니다.");
    }
}
