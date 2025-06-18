package com.nhnacademy.illuwa.domain.address.exception;

public class AddressNotFoundException extends RuntimeException{
    public AddressNotFoundException(Long addressId) {
        super("해당 주소를 찾을 수 없습니다: " + addressId);
    }

    public AddressNotFoundException() {
        super("해당 주소를 찾을 수 없습니다");
    }

    public AddressNotFoundException(String message){
        super(message);
    }

}
