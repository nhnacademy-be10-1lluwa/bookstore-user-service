package com.nhnacademy.illuwa.domain.member.exception;

public class UnauthorizedMemberAccessException extends RuntimeException {
    public UnauthorizedMemberAccessException(String message) {
        super(message);
    }
}
