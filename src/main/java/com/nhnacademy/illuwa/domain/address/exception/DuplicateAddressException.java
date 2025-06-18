package com.nhnacademy.illuwa.domain.address.exception;

public class DuplicateAddressException extends RuntimeException{
    public DuplicateAddressException(String message) {
        super(message);
    }
    public DuplicateAddressException(Long addressId) {
        super("이미 존재하는 주소입니다: " + addressId);
    }
    public DuplicateAddressException() {
        super("이미 존재하는 주소입니다");
    }

}
