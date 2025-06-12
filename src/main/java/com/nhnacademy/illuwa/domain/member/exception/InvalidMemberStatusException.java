package com.nhnacademy.illuwa.domain.member.exception;

public class InvalidMemberStatusException extends RuntimeException {
    public InvalidMemberStatusException(String status) {
        super("유효하지 않은 회원 상태입니다: " + status);
    }
}
