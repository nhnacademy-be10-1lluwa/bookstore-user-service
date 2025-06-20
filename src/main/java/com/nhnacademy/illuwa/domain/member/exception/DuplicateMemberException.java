package com.nhnacademy.illuwa.domain.member.exception;

public class DuplicateMemberException extends RuntimeException {
    public DuplicateMemberException() {
        super("이미 존재하는 회원입니다.");
    }
    public DuplicateMemberException(Long memberId) {
        super("이미 존재하는 회원입니다: " + memberId);
    }

}
