package com.nhnacademy.illuwa.domain.member.exception;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
    public InvalidRequestException() {
        super("잘못된 입력값이 있습니다.");
    }
}
