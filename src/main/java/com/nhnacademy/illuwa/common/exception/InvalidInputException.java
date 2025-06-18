package com.nhnacademy.illuwa.common.exception;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
    public InvalidInputException() {
        super("잘못된 입력값이 있습니다.");
    }
}
