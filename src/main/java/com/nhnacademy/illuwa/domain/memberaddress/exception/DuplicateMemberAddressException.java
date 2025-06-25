package com.nhnacademy.illuwa.domain.memberaddress.exception;

public class DuplicateMemberAddressException extends RuntimeException{
    public DuplicateMemberAddressException(String message) {
        super(message);
    }
    public DuplicateMemberAddressException(Long addressId) {
        super("이미 존재하는 주소입니다: " + addressId);
    }
    public DuplicateMemberAddressException() {
        super("이미 존재하는 주소입니다");
    }

}
