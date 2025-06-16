package com.nhnacademy.illuwa.domain.guest.exception;

public class GuestNotFoundException extends RuntimeException {
    public GuestNotFoundException(String message) {
        super(message);
    }
    public GuestNotFoundException(Long guestId) {
        super("존재하지 않는 비회원입니다: " + guestId);
    }
}