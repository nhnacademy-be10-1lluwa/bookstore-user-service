package com.nhnacademy.illuwa.domain.memberaddress.exception;

public class MemberAddressNotFoundException extends RuntimeException{
    public MemberAddressNotFoundException(Long addressId) {
        super("해당 주소를 찾을 수 없습니다: " + addressId);
    }

    public MemberAddressNotFoundException() {
        super("해당 주소를 찾을 수 없습니다");
    }

    public MemberAddressNotFoundException(String message){
        super(message);
    }

}
