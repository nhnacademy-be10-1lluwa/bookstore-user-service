package com.nhnacademy.illuwa.domain.memberaddress.exception;

public class TooManyMemberAddressException extends RuntimeException{
    public TooManyMemberAddressException() {
        super("주소는 10개까지 등록이 가능합니다");
    }
}