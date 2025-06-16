package com.nhnacademy.illuwa.domain.address.exception;

public class AddressAlreadyExistsException extends RuntimeException{
    public AddressAlreadyExistsException() {
        super("이미 존재하는 주소입니다.");
    }

    public AddressAlreadyExistsException(String message) {
        super(message);
    }

}
