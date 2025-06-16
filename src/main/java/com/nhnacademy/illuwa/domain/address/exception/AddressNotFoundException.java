package com.nhnacademy.illuwa.domain.address.exception;

public class AddressNotFoundException extends RuntimeException{
    public AddressNotFoundException() {
        super("주소를 찾을 수 없습니다.");
    }

    public AddressNotFoundException(String message) {
        super(message);
    }

}
