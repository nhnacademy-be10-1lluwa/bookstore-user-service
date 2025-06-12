package com.nhnacademy.illuwa.domain.member.exception;

public class DuplicateMemberException extends RuntimeException {
    public DuplicateMemberException(String username) {
        super("이미 존재하는 회원입니다. 사용자명: " + username);
    }
}
