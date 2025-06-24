package com.nhnacademy.illuwa.domain.member.exception;

public class DeletedMemberException extends RuntimeException {
    public DeletedMemberException() {
        super("탈퇴한 회원입니다.");
    }
}
